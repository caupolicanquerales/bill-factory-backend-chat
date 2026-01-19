package com.capo.facturas_sinteticas.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.capo.facturas_sinteticas.response.DataMessage;
import com.capo.facturas_sinteticas.utils.ConverterUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ExecutingPromptService {
	
	private final ChatClient chatClient;
	private final MessageToChatClientService messageToChat;
	private final VectorStore vectorStore; 
	
	@Value(value="${event.name.chat}")
	private String eventName;
	
	public ExecutingPromptService(ChatClient.Builder chatClientBuilder,
			MessageToChatClientService messageToChat, VectorStore vectorStore) {
		this.chatClient = chatClientBuilder.build();
		this.messageToChat=messageToChat;
		this.vectorStore= vectorStore;
	}
	
	
	
	public Flux<ServerSentEvent<DataMessage>> executingPrompt(String prompt, StoreFilesService storeFiles){
		Map<String,byte[]> files= Optional.ofNullable(storeFiles.getFileParts()).orElse(new HashMap<String,byte[]>());
		storeFiles.setFileParts(new HashMap<String,byte[]>());
		Mono<String> ragContentMono =getRagContent(prompt);
		return ragContentMono.flatMapMany(ragContent -> {
			Mono<List<Message>> messageMono = messageToChat.buildMessage(prompt, files, ragContent); 
	        return messageMono.flatMapMany(listMessage -> {
	            return this.chatClient.prompt()
	                    .messages(listMessage) 
	                    .stream()
	                    .chatResponse()
	                    .map(this::getTokenMessage);
	        });
	    })
	    .map(ConverterUtil::setDataMessage)
	    .map(data -> ConverterUtil.setServerSentEvent(data, eventName))
	    .doOnComplete(() -> System.out.println("AI Stream Finished. Sending completion flag..."))
	    .concatWith(Flux.defer(() -> {
	        DataMessage finalMsg = ConverterUtil.setDataMessage(eventName + "-COMPLETED");
	        return Flux.just(ConverterUtil.setServerSentEvent(finalMsg, eventName));
	    }))
	    .doOnTerminate(() -> System.out.println("HTTP Response fully closed on server"))
	    .onErrorResume(WebClientResponseException.class, e -> {
	        String errorBody = e.getResponseBodyAsString();
	        System.err.println("OpenAI 400 Error Body: {}" + e);
	        return Flux.error(new RuntimeException("OpenAI API call failed: " + errorBody, e));
	    });
		
	}
	
	private String getTokenMessage(ChatResponse chatResponse) {
        Generation generation = chatResponse.getResult();
        if (generation == null) {
            return "";
        }
        AssistantMessage output = generation.getOutput();
        if (output == null) {
            return "";
        }
        String content = output.getText();
        return (content != null) ? content : "";
    }
	
	
	private Mono<String> getRagContent(String prompt){
		return Mono.fromCallable(()->{
			List<Document> documents = vectorStore.similaritySearch(prompt); 
	        return documents.stream()
	            .map(doc-> doc.getFormattedContent())
	            .collect(Collectors.joining("\n---\n"));
		})
		.subscribeOn(Schedulers.boundedElastic());
	}
	
}
