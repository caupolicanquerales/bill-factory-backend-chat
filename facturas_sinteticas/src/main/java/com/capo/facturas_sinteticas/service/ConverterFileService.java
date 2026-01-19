package com.capo.facturas_sinteticas.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ConverterFileService {
	
	private final FilePartConverterService filePartConverter; 
	
	public ConverterFileService(FilePartConverterService filePartConverter) {
		this.filePartConverter= filePartConverter;
	}
	
	public Mono<Map<String, byte[]>> convertFileToMap(List<FilePart> fileParts) {
	    return Flux.fromIterable(fileParts)
	        .flatMap(filePart -> 
	        filePartConverter.toByteArray(filePart) 
	                .map(bytes -> Map.entry(filePart.filename(), bytes))
	        )
	        .collectMap(Map.Entry::getKey, Map.Entry::getValue);
	}
}
