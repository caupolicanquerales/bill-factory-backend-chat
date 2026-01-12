package com.capo.facturas_sinteticas.service;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class FilePartConverterService {

    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    public Mono<byte[]> toByteArray(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    int dataBufferLength = dataBuffer.readableByteCount();
                    byte[] bytes = new byte[dataBufferLength];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .defaultIfEmpty(new byte[0]);
    }

    public Mono<String> toString(FilePart filePart) {
        return toString(filePart, DEFAULT_ENCODING);
    }

    public Mono<String> toString(FilePart filePart, Charset charset) {
        return toByteArray(filePart)
                .map(bytes -> new String(bytes, charset))
                .defaultIfEmpty("");
    }
}
