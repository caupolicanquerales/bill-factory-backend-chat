package com.capo.facturas_sinteticas.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;

import com.capo.facturas_sinteticas.response.GenerationSyntheticDataResponse;
import com.capo.facturas_sinteticas.response.DataMessage;
import com.capo.facturas_sinteticas.service.sse.SseFileQdrantService;

import reactor.core.publisher.Flux;

public class QdrantControllerTest {

    @Test
    public void testSaveFileInQdrantCallsServiceAndReturnsOk() {
        SseFileQdrantService sse = mock(SseFileQdrantService.class);
        QdrantController ctrl = new QdrantController(sse);
        FilePart fp = mock(FilePart.class);

        ResponseEntity<GenerationSyntheticDataResponse> resp = ctrl.saveFileInQdrant(fp).block();
        assertNotNull(resp);
        assertEquals("OK", resp.getBody().getResponse());
        verify(sse).sendFilePart(any());
    }

    @Test
    public void testStreamMessageFileMapsFluxToSse() {
        SseFileQdrantService sse = mock(SseFileQdrantService.class);
        when(sse.getFilePartStream()).thenReturn(Flux.just(true, false));
        QdrantController ctrl = new QdrantController(sse);

        ServerSentEvent<DataMessage> first = ctrl.streamMessageFile().blockFirst();
        assertNotNull(first);
        assertEquals("true", first.data().getMessage());
    }
}
