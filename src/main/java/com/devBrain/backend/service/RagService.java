package com.devBrain.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RagService {

    private final QdrantSearchService qdrantSearchService;
    private final LlmService llmService;

    public RagResponse ask(String question, int topK) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question must not be empty");
        }

        // Qdrant REST results
        List<Map<String, Object>> results =
                qdrantSearchService.search(question, topK);

        StringBuilder contextBuilder = new StringBuilder();
        List<RagSource> sources = new ArrayList<>();

        for (Map<String, Object> hit : results) {

            String id = String.valueOf(hit.get("id"));
            Map<String, Object> payload =
                    (Map<String, Object>) hit.get("payload");

            String text = payload != null
                    ? String.valueOf(payload.getOrDefault("text", ""))
                    : "";

            contextBuilder
                    .append(text)
                    .append("\n---\n");

            sources.add(new RagSource(
                    id,
                    text,
                    payload
            ));
        }

        String answer =
                llmService.askGroq(question, contextBuilder.toString());

        return new RagResponse(answer, sources);
    }

    public record RagSource(
            String id,
            String content,
            Map<String, Object> metadata
    ) {}

    public record RagResponse(
            String answer,
            List<RagSource> sources
    ) {}
}
