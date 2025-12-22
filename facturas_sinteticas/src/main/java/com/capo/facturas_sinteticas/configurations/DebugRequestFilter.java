package com.capo.facturas_sinteticas.configurations;

import java.util.Objects;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DebugRequestFilter implements WebFilter {

    private static final String FILE_UPLOAD_PATH = "/qdrant/save-file"; // Your controller's path

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        if (!path.endsWith(FILE_UPLOAD_PATH)) {
            return chain.filter(exchange);
        }
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);

        System.out.println("\n=======================================================");
        System.out.println("   [DEBUG FILTER] Incoming request to: " + path);
        System.out.println("   Content-Type: " + contentType);
        
        if (Objects.isNull(contentType)) {
            System.err.println("   [DEBUG FILTER] ERROR: Content-Type header is MISSING.");
        } else if (contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            if (contentType.toLowerCase().contains("boundary=")) {
                System.out.println("   [DEBUG FILTER] OK: Content-Type includes 'boundary=' parameter.");
            } else {
                System.err.println("   [DEBUG FILTER] !!! CRITICAL ERROR !!!");
                System.err.println("   [DEBUG FILTER] Content-Type is multipart/form-data BUT is MISSING the 'boundary=' parameter.");
                System.err.println("   [DEBUG FILTER] The corruption happened EXTERNAL to the application (Proxy/Interceptor/Network).");
            }
        } else {
            System.err.println("   [DEBUG FILTER] WARNING: Content-Type is NOT multipart/form-data: " + contentType);
        }
        
        System.out.println("=======================================================\n");
        return chain.filter(exchange);
    }
}