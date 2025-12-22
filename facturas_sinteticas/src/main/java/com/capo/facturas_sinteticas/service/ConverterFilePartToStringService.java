package com.capo.facturas_sinteticas.service;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class ConverterFilePartToStringService {
	
	private static final String DEFAULT_ENCODING = "UTF-8";
	
	public Mono<String> convertFilePartToString(FilePart filePart) {    
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    int dataBufferLength = dataBuffer.readableByteCount();
                    byte[] bytes = new byte[dataBufferLength];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return new String(bytes, java.nio.charset.Charset.forName(DEFAULT_ENCODING));
                })
                .defaultIfEmpty("");
    }
}
