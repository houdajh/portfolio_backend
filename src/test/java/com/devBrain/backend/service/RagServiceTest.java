package com.devBrain.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RagServiceTest {

    private static final String UNAVAILABLE = "assistant unavailable";

    private final QdrantSearchService qdrant = mock(QdrantSearchService.class);
    private final LlmService llm = mock(LlmService.class);
    private final RagService ragService = new RagService(qdrant, llm, UNAVAILABLE);

    @Test
    void returnsFallbackWhenVectorStoreIsUnavailable() {
        when(qdrant.search(anyString(), anyInt()))
                .thenThrow(new VectorStoreUnavailableException("down", new RuntimeException()));

        var response = ragService.ask("hello?", 5);

        assertEquals(UNAVAILABLE, response.answer());
        assertTrue(response.sources().isEmpty());
        verifyNoInteractions(llm);
    }

    @Test
    void answersWithContextWhenVectorStoreWorks() {
        when(qdrant.search(anyString(), anyInt())).thenReturn(List.of(new Document("some fact")));
        when(llm.askGroq(anyString(), anyString())).thenReturn("an answer");

        var response = ragService.ask("hello?", 5);

        assertEquals("an answer", response.answer());
        assertEquals(1, response.sources().size());
    }
}
