package com.capo.facturas_sinteticas.configurations;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

public class DebugRequestFilterTest {

    @Test
    public void testFilterNonMatchingPathCallsChain() {
        DebugRequestFilter filter = new DebugRequestFilter();
        MockServerHttpRequest request = MockServerHttpRequest.post("/other/path").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }

    @Test
    public void testFilterMatchingPathWithMissingContentType() {
        DebugRequestFilter filter = new DebugRequestFilter();
        MockServerHttpRequest request = MockServerHttpRequest.post("/qdrant/save-file").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = mock(WebFilterChain.class);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();
        verify(chain).filter(exchange);
    }
}
