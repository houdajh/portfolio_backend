package com.devBrain.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(
        name = "app.rag.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class RagService {

    private static final Logger log = LoggerFactory.getLogger(RagService.class);

    private final QdrantSearchService qdrantSearchService;
    private final LlmService llmService;
    private final String unavailableMessage;

    public RagService(
            QdrantSearchService qdrantSearchService,
            LlmService llmService,
            @Value("${app.rag.unavailable-message}") String unavailableMessage
    ) {
        this.qdrantSearchService = qdrantSearchService;
        this.llmService = llmService;
        this.unavailableMessage = unavailableMessage;
    }

    public RagResponse ask(String question, int topK) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question must not be empty");
        }

        List<Document> documents;
        try {
            documents = qdrantSearchService.search(question, topK);
        } catch (VectorStoreUnavailableException e) {
            log.warn("Vector store unavailable, returning fallback answer", e);
            return new RagResponse(unavailableMessage, List.of());
        }

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
