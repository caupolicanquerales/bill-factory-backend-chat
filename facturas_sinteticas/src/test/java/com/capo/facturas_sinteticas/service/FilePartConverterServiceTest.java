package com.capo.facturas_sinteticas.service;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilePartConverterServiceTest {

    @Test
    public void testToByteArrayAndToString() {
        FilePart filePart = mock(FilePart.class);
        byte[] bytes = "hola mundo".getBytes(StandardCharsets.UTF_8);
        DataBuffer buf = new DefaultDataBufferFactory().wrap(bytes);
        when(filePart.content()).thenReturn(Flux.just(buf));

        FilePartConverterService svc = new FilePartConverterService();

        StepVerifier.create(svc.toByteArray(filePart))
            .assertNext(b -> assertArrayEquals(bytes, b))
            .verifyComplete();

        StepVerifier.create(svc.toString(filePart))
            .expectNext("hola mundo")
            .verifyComplete();
    }
}
