package com.capo.facturas_sinteticas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RagService {
	
	private static final Logger log = LoggerFactory.getLogger(RagService.class);
	
	private final ConverterFilePartToStringService converter;
	private final TokenTextSplitter textSplitter;
	private final VectorStore vectorStore; 
	
	public RagService(ConverterFilePartToStringService converter, TokenTextSplitter textSplitter,
			VectorStore vectorStore) {
		this.converter=converter;
		this.textSplitter= textSplitter;
		this.vectorStore=vectorStore;
	}
	
	public Mono<Boolean> executingUpSert(Mono<FilePart> filePartMono) {
		
		Mono<Document> documentMono= filePartMono
			.doOnNext(filePart -> {
				log.info("FILE EMITTED: FilePart received. Filename: {}", filePart.filename());
			})
	        .doOnError(e -> log.error("FILE ERROR: filePartMono emitted an error: {}", e.getMessage(), e))
	        .switchIfEmpty(Mono.defer(() -> {
	            log.error("FILE EMPTY: filePartMono completed as empty. Check request type (must be multipart/form-data) or file size limits.");
	            return Mono.error(new IllegalStateException("File upload failed: FilePart is empty or missing."));
	        }))
		.flatMap(converter::convertFilePartToString)
		.map(text->{
			Document document= new Document(text);
			document.getMetadata().put("source", "automatic-embedding");
			return document;
		});
		
		return documentMono.map(document-> textSplitter.split(document))
			.flatMap(documents -> 
		        Mono.fromRunnable(() -> {
		            vectorStore.add(documents); 
		        })
	        .subscribeOn(Schedulers.boundedElastic())
	        .then(Mono.just(Boolean.TRUE))
        );
	}
	
}
