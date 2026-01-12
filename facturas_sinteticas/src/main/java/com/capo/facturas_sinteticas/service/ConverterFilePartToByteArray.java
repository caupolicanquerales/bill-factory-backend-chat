package com.capo.facturas_sinteticas.service;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class ConverterFilePartToByteArray {
    private final FilePartConverterService converterService;

    public ConverterFilePartToByteArray(FilePartConverterService converterService) {
        this.converterService = converterService;
    }

    public Mono<byte[]> convertFilePartToByteArray(FilePart filePart) {
        return converterService.toByteArray(filePart);
    }
}
