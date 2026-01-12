package com.capo.facturas_sinteticas.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.codec.multipart.FilePart;

import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConverterFilePartToByteArrayTest {

    @Test
    public void testDelegatesToService() {
        FilePartConverterService delegate = mock(FilePartConverterService.class);
        FilePart filePart = mock(FilePart.class);
        when(delegate.toByteArray(filePart)).thenReturn(reactor.core.publisher.Mono.just(new byte[]{1,2,3}));

        ConverterFilePartToByteArray wrapper = new ConverterFilePartToByteArray(delegate);

        StepVerifier.create(wrapper.convertFilePartToByteArray(filePart))
            .assertNext(b -> assertArrayEquals(new byte[]{1,2,3}, b))
            .verifyComplete();
    }
}
