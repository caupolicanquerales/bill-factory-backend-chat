package com.capo.facturas_sinteticas.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.codec.multipart.FilePart;

import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConverterFilePartToStringServiceTest {

    @Test
    public void testConvertFilePartToStringDelegates() {
        FilePartConverterService delegate = mock(FilePartConverterService.class);
        FilePart filePart = mock(FilePart.class);
        when(delegate.toString(org.mockito.Mockito.eq(filePart), org.mockito.Mockito.any()))
            .thenReturn(reactor.core.publisher.Mono.just("abc"));

        ConverterFilePartToStringService svc = new ConverterFilePartToStringService(delegate);

        StepVerifier.create(svc.convertFilePartToString(filePart))
            .expectNext("abc")
            .verifyComplete();
    }
}
