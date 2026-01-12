package com.capo.facturas_sinteticas.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import com.capo.facturas_sinteticas.utils.ExtensionUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MessageToChatClientService {
	private final FilePartConverterService filePartConverter;

	public MessageToChatClientService(FilePartConverterService filePartConverter) {
		this.filePartConverter = filePartConverter;
	}

	public Mono<List<Message>> buildMessage(String prompt, List<FilePart> fileParts, String ragContent) {
		Flux<Object> contentFlux = Flux.fromIterable(fileParts)
				.flatMap(file -> {
		            MimeType mimeType = ExtensionUtils.getMapMimeType(file.filename());
					Mono<byte[]> contentMono = filePartConverter.toByteArray(file);
		            if (Objects.nonNull(mimeType) && mimeType.getType().equals("text")) {
		                return contentMono.map(bytes -> 
		                    "\n\n--- FILE: " + file.filename() + " (" + mimeType.getSubtype() + ") ---\n" + 
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
