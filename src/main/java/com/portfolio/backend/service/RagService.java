package com.portfolio.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
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

        List<Document> documents = qdrantSearchService.search(question, topK);

        StringBuilder contextBuilder = new StringBuilder();
        List<RagSource> sources = new ArrayList<>();

        for (Document doc : documents) {
            contextBuilder
                    .append(doc.getText())
                    .append("\n---\n");

            sources.add(new RagSource(
                    doc.getId(),
                    doc.getText(),
                    doc.getMetadata()
            ));
        }

        String answer = llmService.askGroq(question, contextBuilder.toString());

        return new RagResponse(answer, sources);
    }

    public record RagSource(String id, String content, Map<String, Object> metadata) {
    }

    public record RagResponse(String answer, List<RagSource> sources) {
    }
}
