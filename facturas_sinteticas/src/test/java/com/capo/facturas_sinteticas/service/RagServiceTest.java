package com.capo.facturas_sinteticas.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class RagServiceTest {

    @Test
    public void testExecutingUpSertAddsToVectorStore() {
        FilePartConverterService converter = mock(FilePartConverterService.class);
        TokenTextSplitter splitter = mock(TokenTextSplitter.class);
        VectorStore vectorStore = mock(VectorStore.class);

        FilePart filePart = mock(FilePart.class);
        when(converter.toString(filePart)).thenReturn(Mono.just("doc text"));

        Document doc = new Document("doc text");
        when(splitter.split(org.mockito.Mockito.any(org.springframework.ai.document.Document.class))).thenReturn(List.of(doc));

        RagService svc = new RagService(converter, splitter, vectorStore);

        StepVerifier.create(svc.executingUpSert(Mono.just(filePart)))
            .expectNext(true)
            .verifyComplete();

        verify(vectorStore).add(List.of(doc));
    }
}
