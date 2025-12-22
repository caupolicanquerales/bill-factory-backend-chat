package com.capo.facturas_sinteticas.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.capo.facturas_sinteticas.response.DataMessage;
import com.capo.facturas_sinteticas.response.GenerationSyntheticDataResponse;
import com.capo.facturas_sinteticas.service.sse.SseFileQdrantService;
import com.capo.facturas_sinteticas.utils.ConverterUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("qdrant")
@CrossOrigin(origins = "http://localhost:4200")
public class QdrantController {
	
	private final SseFileQdrantService sseFileQdrant;
	
	@Value(value="${event.name.file}")
	private String eventName;
	
	public QdrantController(SseFileQdrantService sseFileQdrant) {
		this.sseFileQdrant= sseFileQdrant;
	}
	
	@PostMapping("/save-file")
	public Mono<ResponseEntity<GenerationSyntheticDataResponse>> saveFileInQdrant(@RequestPart("file") FilePart filePartMono){
		Mono<FilePart> cachedMono = Mono.just(filePartMono).cache();
		sseFileQdrant.sendFilePart(cachedMono);
		return Mono.just(new String("OK"))
				.map(ConverterUtil::getGenerationSyntheticDataResponse)
				.map(ResponseEntity.ok()::body)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}
	
	
	@GetMapping(path = "/stream-file", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<DataMessage>> streamMessageFile() {
        return sseFileQdrant.getFilePartStream()
    		.filter(message -> Objects.nonNull(message))
    		.map(result->String.valueOf(result))
            .map(ConverterUtil::setDataMessage)
            .map(data->ConverterUtil.setServerSentEvent(data, eventName));
    }
}
