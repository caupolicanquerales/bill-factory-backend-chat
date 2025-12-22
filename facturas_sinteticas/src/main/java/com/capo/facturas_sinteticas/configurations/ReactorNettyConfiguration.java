package com.capo.facturas_sinteticas.configurations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient; // Added Import

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class ReactorNettyConfiguration {
	
	// Use Duration for clarity and consistency. Set to 5 minutes (300 seconds).
		private static final Duration API_TIMEOUT = Duration.ofMinutes(5); 
		
		@Bean
	    public ConnectionProvider connectionProvider() {
			// Sets the maximum time a connection can remain idle in the pool.
			// This should be longer than the max expected request time.
	        return ConnectionProvider.builder("openai-dalle-pool")
	                .maxIdleTime(API_TIMEOUT.plusSeconds(60))
	                .build();
	    }
		
		@Bean
	    public ReactorClientHttpConnector reactorClientHttpConnector(ConnectionProvider connectionProvider) {
	        
			HttpClient httpClient = HttpClient.create(connectionProvider)
		            // 1. TCP Connection Timeout (10 seconds to establish the connection)
		            .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
					
					// 2. HTTP Response Timeout (Crucial for long latency calls like DALL-E)
					// This times out the overall request if the response isn't completed in 5 minutes.
					.responseTimeout(API_TIMEOUT) 
		            
					// 3. Socket Inactivity Handlers (monitors data flow on the connected socket)
		            .doOnConnected(connection -> {
		                connection.addHandlerLast("readTimeoutHandler", 
		                    new ReadTimeoutHandler(API_TIMEOUT.toSeconds(), TimeUnit.SECONDS));
		                
		                connection.addHandlerLast("writeTimeoutHandler", 
		                    new WriteTimeoutHandler(API_TIMEOUT.toSeconds(), TimeUnit.SECONDS));
		            });

		        return new ReactorClientHttpConnector(httpClient);
	    }
	    
	    /**
	     * Exposes the WebClient.Builder required for Spring AI's reactive 
	     * ChatClient auto-configuration (ChatClientAutoConfiguration) to find the 
	     * necessary component when running in WebFlux mode.
	     */
	    @Bean
	    public WebClient.Builder webClientBuilder(ReactorClientHttpConnector connector) {
	        return WebClient.builder().clientConnector(connector);
	    }
}
