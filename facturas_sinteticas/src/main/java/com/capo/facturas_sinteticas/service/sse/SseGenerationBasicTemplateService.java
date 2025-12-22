package com.capo.facturas_sinteticas.service.sse;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;

@Service
public class SseGenerationBasicTemplateService {
	
	private final Sinks.Many<String> dataSink = Sinks.many().multicast().onBackpressureBuffer();

	public void sendMessage(String message) {
        EmitResult result = dataSink.tryEmitNext(message); 
        
        if (result.isFailure()) {
            System.err.println("Failed to emit message: " + result);
        }
    }
	
	public Flux<String> getDataMessageStream() {
        return dataSink.asFlux().log();
    }

}
