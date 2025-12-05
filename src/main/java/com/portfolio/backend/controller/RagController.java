package com.portfolio.backend.controller;

import com.portfolio.backend.service.EmbeddingService;
import com.portfolio.backend.service.LlmService;
import com.portfolio.backend.service.QdrantSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final QdrantSearchService qdrant;
    private final EmbeddingService embeddingService;
    private final LlmService llmService;

    public RagController(QdrantSearchService qdrant, EmbeddingService embeddingService, LlmService llmService) {
        this.qdrant = qdrant;
        this.embeddingService = embeddingService;
        this.llmService = llmService;
    }


    @PostMapping("/test-embed")
    public float[] testEmbed(@RequestBody Map<String, String> body) throws Exception {
        return embeddingService.embed(body.get("text"));
    }


    @PostMapping("/search")
    public List<Map<String, Object>> searchRag(@RequestBody Map<String, String> body) throws Exception {

        String question = body.get("question");

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Missing field: question");
        }

        // 1) Embed the question
        float[] embeddings = embeddingService.embed(question);

        // 2) Search Qdrant
        return qdrant.search(embeddings);
    }



    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody Map<String, String> body) throws Exception {

        String question = body.get("question");

        // 1) Embed + Qdrant
        float[] embedding = embeddingService.embed(question);
        List<Map<String, Object>> docs = qdrant.search(embedding);

        // 2) Build context from payload
        StringBuilder context = new StringBuilder();
        for (Map<String, Object> doc : docs) {
            Map payload = (Map) doc.get("payload");
            if (payload != null && payload.get("text") != null) {
                context.append(payload.get("text")).append("\n\n");
            }
        }

        // 3) Ask Groq
        String answer = llmService.askGroq(question, context.toString());

        return Map.of(
                "answer", answer,
                "sources", docs
        );
    }


}
