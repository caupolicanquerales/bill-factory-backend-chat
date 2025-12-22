package com.capo.facturas_sinteticas.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.capo.facturas_sinteticas.response.DataMessage;
import com.capo.facturas_sinteticas.utils.ConverterUtil;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ExecutingPromptExtractTemplateService {
	
	private final ChatClient chatClient;
	private final MessageToChatClientService messageToChat;
	
	@Value(value="${event.name.chat.basic.template}")
	private String eventName;
	
	public ExecutingPromptExtractTemplateService(ChatClient.Builder chatClientBuilder,
			MessageToChatClientService messageToChat) {
		this.chatClient = chatClientBuilder.build();
		this.messageToChat=messageToChat;
	}
	
	
	
	public Mono<ServerSentEvent<DataMessage>> executingPrompt(String prompt, StoreFilesService storeFiles){
		List<FilePart> files= Optional.ofNullable(storeFiles.getFileParts()).orElse(new ArrayList<FilePart>());
		storeFiles.setFileParts(new ArrayList<FilePart>());
		Mono<String> ragContentMono = Mono.just("");
		return ragContentMono.flatMap(ragContent -> {
			Mono<List<Message>> messageMono = messageToChat.buildMessage(prompt, files, ragContent); 
			return messageMono.flatMap(listMessage -> {
                Mono<String> contentMono = Mono.fromCallable(() -> 
                    this.chatClient.prompt()
                        .messages(listMessage)
                        .call() 
                        .content() 
                )
                .subscribeOn(Schedulers.boundedElastic()); 
                return contentMono.map(content -> {
                    DataMessage data = ConverterUtil.setDataMessage(content);
                    return ConverterUtil.setServerSentEvent(data, eventName);
                });
            });
		
	    })
	    .onErrorResume(WebClientResponseException.class, e -> {
	        String errorBody = e.getResponseBodyAsString();
	        System.err.println("OpenAI 400 Error Body: {}" + e);
	        return Mono.error(new RuntimeException("OpenAI API call failed: " + errorBody, e));
	    });
	}
	
}
