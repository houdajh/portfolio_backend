package com.devBrain.backend.controller;

import com.devBrain.backend.service.LlmService;
import com.devBrain.backend.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ask")
public class AskController {

    private final LlmService llmService;
    private final RagService ragService; // optional

    public AskController(
            LlmService llmService,
            @Autowired(required = false) RagService ragService
    ) {
        this.llmService = llmService;
        this.ragService = ragService;
    }

    @Value("${app.rag.enabled:false}")
    private boolean ragEnabled;

    /* =======================
       DTOs (YOU WERE MISSING THESE)
       ======================= */

    public record AskRequest(String question, Integer topK) {
    }

    public record AskResponse(String answer, List<?> sources) {
    }

    /* =======================
       Endpoint
       ======================= */

    @PostMapping
    public AskResponse ask(@RequestBody AskRequest payload) {

        if (!ragEnabled) {
            // CHAT ONLY (prod)
            return new AskResponse(
                    llmService.askGroq(payload.question()),
                    List.of()
            );
        }

        // RAG MODE (local)
        int topK = payload.topK() != null ? payload.topK() : 5;
        var response = ragService.ask(payload.question(), topK);

        return new AskResponse(response.answer(), response.sources());
    }
}
