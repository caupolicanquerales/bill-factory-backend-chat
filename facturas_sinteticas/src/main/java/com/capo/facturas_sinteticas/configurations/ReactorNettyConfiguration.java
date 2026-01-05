package com.capo.facturas_sinteticas.configurations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class ReactorNettyConfiguration {
	
		private static final Duration API_TIMEOUT = Duration.ofMinutes(5); 
		
		@Bean
	    public ConnectionProvider connectionProvider() {
			return ConnectionProvider.builder("openai-dalle-pool")
	                .maxIdleTime(API_TIMEOUT.plusSeconds(60))
	                .build();
	    }
		
		@Bean
	    public ReactorClientHttpConnector reactorClientHttpConnector(ConnectionProvider connectionProvider) {
	        
			HttpClient httpClient = HttpClient.create(connectionProvider)
		            .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
					.responseTimeout(API_TIMEOUT) 
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
