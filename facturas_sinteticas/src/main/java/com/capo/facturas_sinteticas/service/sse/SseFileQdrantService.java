package com.capo.facturas_sinteticas.service.sse;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.capo.facturas_sinteticas.service.RagService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitResult;

@Service
public class SseFileQdrantService {
	
	private final RagService ragService;
	private final Sinks.Many<Boolean> dataSink = Sinks.many().multicast().onBackpressureBuffer();
	
	public SseFileQdrantService(RagService ragService) {
		this.ragService= ragService;
	}
	
	public void sendFilePart(Mono<FilePart> filePartMono) {
		Mono<Boolean> processingMono = ragService.executingUpSert(filePartMono)
		.doOnSuccess(success -> {
			EmitResult result = dataSink.tryEmitNext(success);
            if (result.isFailure()) {
                System.err.println("Failed to emit SUCCESS result: " + result);
            }
        })
        .doOnError(e -> {
            EmitResult result = dataSink.tryEmitNext(false); 
            if (result.isFailure()) {
                System.err.println("Failed to emit ERROR result: " + result);
            }
        });
		processingMono.subscribe();
    }
	
	public Flux<Boolean> getFilePartStream() {
        return dataSink.asFlux().log();
    }

}
