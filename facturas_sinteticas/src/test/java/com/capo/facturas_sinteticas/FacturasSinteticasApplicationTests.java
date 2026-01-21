package com.capo.facturas_sinteticas;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(FacturasSinteticasApplicationTests.TestBeansConfig.class)
class FacturasSinteticasApplicationTests {

	@Test
	void contextLoads() {
	}

	@TestConfiguration
	static class TestBeansConfig {
		@Bean
		VectorStore vectorStore() {
			return Mockito.mock(VectorStore.class);
		}
	}
}
