package com.capo.facturas_sinteticas.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class StoreFilesServiceTest {

    @Test
    public void testSetAndGetFileParts() {
        StoreFilesService s = new StoreFilesService();
        Map<String, byte[]> files = Map.of("a.html", "content".getBytes());
        s.setFileParts(files);
        assertNotNull(s.getFileParts());
        assertEquals(1, s.getFileParts().size());
        assertTrue(s.getFileParts().containsKey("a.html"));
    }
}
