package com.capo.facturas_sinteticas.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.codec.ServerSentEvent;

import com.capo.facturas_sinteticas.response.DataMessage;
import com.capo.facturas_sinteticas.response.GenerationSyntheticDataResponse;

public class ConverterUtilTest {

    @Test
    public void testGetGenerationSyntheticDataResponse() {
        GenerationSyntheticDataResponse r = ConverterUtil.getGenerationSyntheticDataResponse("hello");
        assertNotNull(r);
        assertEquals("hello", r.getResponse());
    }

    @Test
    public void testSetDataMessage() {
        DataMessage dm = ConverterUtil.setDataMessage("d");
        assertNotNull(dm);
        assertEquals("d", dm.getMessage());
    }

    @Test
    public void testSetServerSentEvent() {
        DataMessage dm = ConverterUtil.setDataMessage("x");
        ServerSentEvent<DataMessage> sse = ConverterUtil.setServerSentEvent(dm, "evt");
        assertNotNull(sse);
        assertEquals("evt", sse.event());
        assertEquals(dm, sse.data());
    }
}
