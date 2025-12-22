package com.capo.facturas_sinteticas.configurations;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TextSplitterConfiguration {
	
	private static final int DEFAULT_MIN_CHUNK_SIZE_CHARS = 1; 
    private static final int DEFAULT_MIN_CHUNK_LENGTH_TO_EMBED = 5; 
    private static final int DEFAULT_MAX_NUM_CHUNKS = 10000; 
    private static final boolean DEFAULT_KEEP_SEPARATOR = true; 
    
	@Bean
    public TokenTextSplitter textSplitter() {
		    int chunkSize = 1536;
		    return new TokenTextSplitter(
		            chunkSize, 
		            DEFAULT_MIN_CHUNK_SIZE_CHARS, 
		            DEFAULT_MIN_CHUNK_LENGTH_TO_EMBED, 
		            DEFAULT_MAX_NUM_CHUNKS, 
		            DEFAULT_KEEP_SEPARATOR
		        );
    }
}
