package com.capo.facturas_sinteticas.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import com.capo.facturas_sinteticas.utils.ExtensionUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MessageToChatClientService {

	
	public Mono<List<Message>> buildMessage(String prompt, Map<String,byte[]> files, String ragContent) {
		Flux<Object> contentFlux = Flux.fromIterable(files.entrySet())
				.flatMap(entry -> {
		            MimeType mimeType = ExtensionUtils.getMapMimeType(entry.getKey());
					Mono<byte[]> contentMono = Mono.just(entry.getValue());
		            if (Objects.nonNull(mimeType) && mimeType.getType().equals("text")) {
		                return contentMono.map(bytes -> 
		                    "\n\n--- FILE: " + entry.getKey() + " (" + mimeType.getSubtype() + ") ---\n" + 
		                    new String(bytes, StandardCharsets.UTF_8) + 
		                    "\n--- END OF FILE ---\n"
		                );
		            } else {
		                return contentMono.map(bytes -> 
		                    new Media(mimeType, new ByteArrayResource(bytes))
		                );
		            }
		        });

		    return contentFlux.collectList()
		        .map(contentList -> {
		            StringBuilder fullText = new StringBuilder(prompt);
		            List<Media> mediaList = new ArrayList<>();
		            for (Object content : contentList) {
		                if (content instanceof String textPart) {
		                    fullText.append(textPart); 
		                } else if (content instanceof Media mediaPart) {
		                    mediaList.add(mediaPart); 
		                }
		            }
		            String systemPromptText= getPromptTextRag(ragContent);
		            SystemMessage systemMessage = new SystemMessage(systemPromptText);
		            UserMessage userMessage = UserMessage.builder()
		                    .media(mediaList)
		                    .text(fullText.toString())
		                    .build();

		            return List.of(systemMessage, userMessage); 
	        });
	}
	
    
	
	private String getPromptTextRag(String ragContent) {
		return """
			    You are an expert assistant.
			    1. **Primary Goal:** Always try to base your answer on the provided CONTEXT first.
			    2. **Secondary Goal:** If the answer is not available or incomplete in the CONTEXT, use your general knowledge to provide a comprehensive answer.
			    
			    CONTEXT:
			    ---
			    %s
			    ---
			    """.formatted(ragContent);
	}
}
