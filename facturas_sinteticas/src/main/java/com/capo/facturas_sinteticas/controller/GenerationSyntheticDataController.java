package com.capo.facturas_sinteticas.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.capo.facturas_sinteticas.request.GenerationSyntheticDataRequest;
import com.capo.facturas_sinteticas.response.DataMessage;
import com.capo.facturas_sinteticas.response.GenerationSyntheticDataResponse;
import com.capo.facturas_sinteticas.service.ExecutingPromptService;
import com.capo.facturas_sinteticas.service.StoreFilesService;
import com.capo.facturas_sinteticas.utils.ConverterUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("generation")
@CrossOrigin(origins = "http://localhost:4200")
public class GenerationSyntheticDataController {
	
	private final ExecutingPromptService executingPrompt;
	private final StoreFilesService storeFiles;
	

    public GenerationSyntheticDataController(ExecutingPromptService executingPrompt, StoreFilesService storeFiles) {
        this.executingPrompt= executingPrompt;
        this.storeFiles= storeFiles;
    }
	
	@PostMapping("/sending-files")
	public Mono<ResponseEntity<GenerationSyntheticDataResponse>> getFilesToChatClient(@RequestPart("files") List<FilePart> fileParts){
		storeFiles.setFileParts(fileParts);
		return Mono.just(new String("OK"))
				.map(ConverterUtil::getGenerationSyntheticDataResponse)
				.map(ResponseEntity.ok()::body)
				.switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
	}
	
	@PostMapping(path = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<DataMessage>> chatClient(@RequestBody GenerationSyntheticDataRequest request) {
		return Flux.just(request.getPrompt())
	    		.filter(message -> Objects.nonNull(message)) 
	            .filter(message ->  !message.trim().isEmpty())
	            .flatMap(prompt->executingPrompt.executingPrompt(prompt, storeFiles));
	}
	
}
