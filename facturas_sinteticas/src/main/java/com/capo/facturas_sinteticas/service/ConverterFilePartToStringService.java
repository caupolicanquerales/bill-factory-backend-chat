package com.capo.facturas_sinteticas.service;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class ConverterFilePartToStringService {
    private final FilePartConverterService converterService;
    private static final String DEFAULT_ENCODING = "UTF-8";

    public ConverterFilePartToStringService(FilePartConverterService converterService) {
        this.converterService = converterService;
    }

    public Mono<String> convertFilePartToString(FilePart filePart) {
        return converterService.toString(filePart, java.nio.charset.Charset.forName(DEFAULT_ENCODING));
    }
}
