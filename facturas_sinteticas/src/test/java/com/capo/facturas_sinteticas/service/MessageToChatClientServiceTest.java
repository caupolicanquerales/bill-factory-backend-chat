package com.capo.facturas_sinteticas.service;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class MessageToChatClientServiceTest {

    @Test
    public void testBuildMessageWithTextFilesMap() {
        byte[] textBytesA = "filecontentA".getBytes(StandardCharsets.UTF_8);
        byte[] textBytesB = "filecontentB".getBytes(StandardCharsets.UTF_8);

        Map<String, byte[]> filesMap = Map.of(
            "a.html", textBytesA,
            "b.css", textBytesB
        );

        MessageToChatClientService svc = new MessageToChatClientService();

        Mono<List<Message>> result = svc.buildMessage("prompt", filesMap, "rag-context");

        StepVerifier.create(result)
            .assertNext(list -> {
                assertEquals(2, list.size());
                assertTrue(list.get(0) instanceof org.springframework.ai.chat.messages.SystemMessage);
                assertTrue(list.get(1) instanceof org.springframework.ai.chat.messages.UserMessage);
                org.springframework.ai.chat.messages.UserMessage u = (org.springframework.ai.chat.messages.UserMessage) list.get(1);
                assertTrue(u.getText().contains("prompt"));
                assertTrue(u.getText().contains("filecontentA"));
                assertTrue(u.getText().contains("filecontentB"));
                // text files should be appended to text; no media for these types
                assertTrue(u.getMedia().isEmpty());
            })
            .verifyComplete();
    }

    @Test
    public void testBuildMessageWithImageAsMedia() {
        byte[] imageBytes = new byte[] { (byte)0x89, 0x50, 0x4E, 0x47 }; // PNG header bytes

        Map<String, byte[]> filesMap = Map.of(
            "image.png", imageBytes
        );

        MessageToChatClientService svc = new MessageToChatClientService();

        Mono<List<Message>> result = svc.buildMessage("prompt", filesMap, "rag-context");

        StepVerifier.create(result)
            .assertNext(list -> {
                assertEquals(2, list.size());
                assertTrue(list.get(0) instanceof org.springframework.ai.chat.messages.SystemMessage);
                assertTrue(list.get(1) instanceof org.springframework.ai.chat.messages.UserMessage);
                org.springframework.ai.chat.messages.UserMessage u = (org.springframework.ai.chat.messages.UserMessage) list.get(1);
                // image should be represented as media, not inlined in text
                assertTrue(u.getMedia() != null && !u.getMedia().isEmpty());
                assertEquals(1, u.getMedia().size());
                assertEquals("image", u.getMedia().get(0).getMimeType().getType());
                assertEquals("png", u.getMedia().get(0).getMimeType().getSubtype());
            })
            .verifyComplete();
    }
}
