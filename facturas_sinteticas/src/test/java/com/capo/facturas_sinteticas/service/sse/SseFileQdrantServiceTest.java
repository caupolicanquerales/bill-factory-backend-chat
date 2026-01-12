package com.capo.facturas_sinteticas.service.sse;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.codec.multipart.FilePart;

import com.capo.facturas_sinteticas.service.RagService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class SseFileQdrantServiceTest {

    @Test
    public void testSendFilePartEmitsSinkOnSuccess() {
        RagService rag = mock(RagService.class);
        when(rag.executingUpSert(any())).thenReturn(Mono.just(Boolean.TRUE));

        SseFileQdrantService svc = new SseFileQdrantService(rag);

        svc.sendFilePart(Mono.just(mock(FilePart.class)));

        StepVerifier.create(svc.getFilePartStream().take(1))
            .expectNext(Boolean.TRUE)
            .verifyComplete();
    }

    @Test
    public void testSendFilePartEmitsFalseOnError() {
        RagService rag = mock(RagService.class);
        when(rag.executingUpSert(any())).thenReturn(Mono.error(new RuntimeException("fail")));

        SseFileQdrantService svc = new SseFileQdrantService(rag);

        svc.sendFilePart(Mono.just(mock(FilePart.class)));

        StepVerifier.create(svc.getFilePartStream().take(1))
            .expectNext(Boolean.FALSE)
            .verifyComplete();
    }
}
