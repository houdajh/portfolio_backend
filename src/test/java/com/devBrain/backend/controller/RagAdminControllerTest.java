package com.devBrain.backend.controller;

import com.devBrain.backend.service.RagIndexer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RagAdminController.class)
@TestPropertySource(properties = "app.rag.admin-token=test-token")
class RagAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RagIndexer indexer;

    @Test
    void reindexWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/rag/admin/reindex"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(indexer);
    }

    @Test
    void reindexWithWrongTokenIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/rag/admin/reindex").header("X-Admin-Token", "wrong"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(indexer);
    }

    @Test
    void reindexWithCorrectTokenRunsIndexer() throws Exception {
        mockMvc.perform(post("/api/rag/admin/reindex").header("X-Admin-Token", "test-token"))
                .andExpect(status().isOk());

        verify(indexer).reindex();
    }
}
