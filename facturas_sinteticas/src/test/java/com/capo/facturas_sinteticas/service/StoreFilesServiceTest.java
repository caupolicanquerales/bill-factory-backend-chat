package com.capo.facturas_sinteticas.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.codec.multipart.FilePart;

public class StoreFilesServiceTest {

    @Test
    public void testSetAndGetFileParts() {
        StoreFilesService s = new StoreFilesService();
        FilePart f = org.mockito.Mockito.mock(FilePart.class);
        s.setFileParts(List.of(f));
        assertEquals(1, s.getFileParts().size());
    }
}
