package com.capo.facturas_sinteticas.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;

import com.capo.facturas_sinteticas.request.GenerationSyntheticDataRequest;
import com.capo.facturas_sinteticas.response.DataMessage;
import com.capo.facturas_sinteticas.response.GenerationSyntheticDataResponse;
import com.capo.facturas_sinteticas.service.ExecutingPromptService;
import com.capo.facturas_sinteticas.service.StoreFilesService;
import com.capo.facturas_sinteticas.service.ConverterFileService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GenerationSyntheticDataControllerTest {

    @Test
    public void testGetFilesToChatClientSetsStoreAndReturnsOk() {
        ExecutingPromptService exec = mock(ExecutingPromptService.class);
        StoreFilesService store = new StoreFilesService();
        ConverterFileService converter = mock(ConverterFileService.class);
        GenerationSyntheticDataController ctrl = new GenerationSyntheticDataController(exec, store, converter);
        FilePart f = mock(FilePart.class);

        Map<String, byte[]> filesMap = Map.of("a.html", "content".getBytes());
        when(converter.convertFileToMap(List.of(f))).thenReturn(Mono.just(filesMap));

        ResponseEntity<GenerationSyntheticDataResponse> r = ctrl.getFilesToChatClient(List.of(f)).block();
        assertNotNull(r);
        assertEquals("OK", r.getBody().getResponse());
        assertEquals(1, store.getFileParts().size());
    }

    @Test
    public void testChatClientDelegatesToExecutingPrompt() {
        ExecutingPromptService exec = mock(ExecutingPromptService.class);
        StoreFilesService store = new StoreFilesService();
        ConverterFileService converter = mock(ConverterFileService.class);
        GenerationSyntheticDataController ctrl = new GenerationSyntheticDataController(exec, store, converter);

        GenerationSyntheticDataRequest req = new GenerationSyntheticDataRequest();
        req.setPrompt("hello");

        when(exec.executingPrompt(eq("hello"), eq(store))).thenReturn(Flux.just(ServerSentEvent.builder(new DataMessage()).build()));

        ServerSentEvent<DataMessage> s = ctrl.chatClient(req).blockFirst();
        assertNotNull(s);
    }
}
