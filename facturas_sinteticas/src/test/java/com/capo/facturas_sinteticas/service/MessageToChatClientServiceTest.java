package com.capo.facturas_sinteticas.service;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;

import com.capo.facturas_sinteticas.utils.ExtensionUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageToChatClientServiceTest {

    @Test
    public void testBuildMessageWithTextAndMedia() {
        FilePartConverterService converter = mock(FilePartConverterService.class);
        FilePart textFile = mock(FilePart.class);
        FilePart binFile = mock(FilePart.class);

        when(textFile.filename()).thenReturn("a.html");
        when(binFile.filename()).thenReturn("b.css");

        byte[] textBytes = "filecontentA".getBytes(StandardCharsets.UTF_8);
        byte[] binBytes = "filecontentB".getBytes(StandardCharsets.UTF_8);

        when(converter.toByteArray(textFile)).thenReturn(Mono.just(textBytes));
        when(converter.toByteArray(binFile)).thenReturn(Mono.just(binBytes));

        MessageToChatClientService svc = new MessageToChatClientService(converter);

        Mono<List<org.springframework.ai.chat.messages.Message>> result = svc.buildMessage("prompt", List.of(textFile, binFile), "rag");

        StepVerifier.create(result)
            .assertNext(list -> {
                assertEquals(2, list.size());
                assertTrue(list.get(0) instanceof org.springframework.ai.chat.messages.SystemMessage);
                assertTrue(list.get(1) instanceof org.springframework.ai.chat.messages.UserMessage);
                org.springframework.ai.chat.messages.UserMessage u = (org.springframework.ai.chat.messages.UserMessage) list.get(1);
                assertTrue(u.getText().contains("prompt"));
                assertTrue(u.getText().contains("filecontentA"));
                assertTrue(u.getText().contains("filecontentB"));
            })
            .verifyComplete();
    }
}
