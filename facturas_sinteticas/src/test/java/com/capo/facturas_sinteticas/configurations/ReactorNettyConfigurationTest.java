package com.capo.facturas_sinteticas.configurations;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import reactor.netty.resources.ConnectionProvider;

public class ReactorNettyConfigurationTest {

    @Test
    public void testConnectionProviderAndWebClientBuilder() {
        ReactorNettyConfiguration cfg = new ReactorNettyConfiguration();
        ConnectionProvider cp = cfg.connectionProvider();
        assertNotNull(cp);
        assertNotNull(cfg.reactorClientHttpConnector(cp));
        assertNotNull(cfg.webClientBuilder(cfg.reactorClientHttpConnector(cp)));
    }
}
