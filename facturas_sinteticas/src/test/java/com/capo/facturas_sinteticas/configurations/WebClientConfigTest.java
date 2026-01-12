package com.capo.facturas_sinteticas.configurations;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class WebClientConfigTest {

    @Test
    public void testWebClientBuilderNotNull() {
        WebClientConfig cfg = new WebClientConfig();
        assertNotNull(cfg.webClientBuilder());
    }
}
