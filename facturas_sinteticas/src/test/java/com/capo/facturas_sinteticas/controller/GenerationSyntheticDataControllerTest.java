package com.capo.facturas_sinteticas.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;

import com.capo.facturas_sinteticas.request.GenerationSyntheticDataRequest;
import com.capo.facturas_sinteticas.response.DataMessage;
import com.capo.facturas_sinteticas.response.GenerationSyntheticDataResponse;
import com.capo.facturas_sinteticas.service.ExecutingPromptService;
import com.capo.facturas_sinteticas.service.StoreFilesService;

import reactor.core.publisher.Flux;

public class GenerationSyntheticDataControllerTest {

    @Test
    public void testGetFilesToChatClientSetsStoreAndReturnsOk() {
        ExecutingPromptService exec = mock(ExecutingPromptService.class);
        StoreFilesService store = new StoreFilesService();
        GenerationSyntheticDataController ctrl = new GenerationSyntheticDataController(exec, store);
        FilePart f = mock(FilePart.class);

        ResponseEntity<GenerationSyntheticDataResponse> r = ctrl.getFilesToChatClient(List.of(f)).block();
        assertNotNull(r);
        assertEquals("OK", r.getBody().getResponse());
        assertEquals(1, store.getFileParts().size());
    }

    @Test
    public void testChatClientDelegatesToExecutingPrompt() {
        ExecutingPromptService exec = mock(ExecutingPromptService.class);
        StoreFilesService store = new StoreFilesService();
        GenerationSyntheticDataController ctrl = new GenerationSyntheticDataController(exec, store);

        GenerationSyntheticDataRequest req = new GenerationSyntheticDataRequest();
        req.setPrompt("hello");

        when(exec.executingPrompt(eq("hello"), eq(store))).thenReturn(Flux.just(ServerSentEvent.builder(new DataMessage()).build()));

        ServerSentEvent<DataMessage> s = ctrl.chatClient(req).blockFirst();
        assertNotNull(s);
    }
}
