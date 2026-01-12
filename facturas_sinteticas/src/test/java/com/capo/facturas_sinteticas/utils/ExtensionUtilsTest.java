package com.capo.facturas_sinteticas.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.util.MimeType;

public class ExtensionUtilsTest {

    @Test
    public void testGetMapMimeTypeKnown() {
        MimeType m = ExtensionUtils.getMapMimeType("index.html");
        assertNotNull(m);
        assertEquals("text", m.getType());
        assertEquals("html", m.getSubtype());
    }

    @Test
    public void testGetMapMimeTypeUnknown() {
        MimeType m = ExtensionUtils.getMapMimeType("file.unknownext");
        assertNull(m);
    }
}
