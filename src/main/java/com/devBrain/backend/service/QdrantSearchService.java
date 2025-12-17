package com.devBrain.backend.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class QdrantSearchService {

    private final WebClient qdrantClient;
    private final VoyageEmbeddingService embeddingService;

    public QdrantSearchService(
            WebClient.Builder builder,
            VoyageEmbeddingService embeddingService
    ) {
        System.out.println("QDRANT_URL = " + System.getenv("QDRANT_URL"));
        this.qdrantClient = builder
                .baseUrl(System.getenv("QDRANT_URL"))
                .defaultHeader("api-key", System.getenv("QDRANT_API_KEY"))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();

        this.embeddingService = embeddingService;
    }

    public List<Map<String, Object>> search(String query, int topK) {

        float[] vector = embeddingService.embed(query);
        System.out.println("Embedding size = " + vector.length);

        if (vector.length != 1024) {
            throw new IllegalStateException(
                    "Embedding dimension mismatch: expected 1024, got " + vector.length
            );
        }


        List<Float> vectorList = new ArrayList<>(vector.length);
        for (float v : vector) {
            vectorList.add(v);
        }

        Map<String, Object> body = Map.of(
                "vector", vectorList,
                "limit", topK,
                "with_payload", true
        );


        return qdrantClient.post()
                .uri("/collections/portfolio_docs/points/search")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(resp -> (List<Map<String, Object>>) resp.get("result"))
                .block();
    }

    public void index(String content, Map<String, Object> metadata) {

        float[] vector = embeddingService.embed(content);

        List<Float> vectorList = new java.util.ArrayList<>(vector.length);
        for (float v : vector) {
            vectorList.add(v);
        }

        qdrantClient.put()
                .uri("/collections/portfolio_docs/points")
                .bodyValue(Map.of(
                        "points", List.of(
                                Map.of(
                                        "id", UUID.randomUUID().toString(),
                                        "vector", vectorList,
                                        "payload", metadata
                                )
                        )
                ))
                .retrieve()
                .toBodilessEntity()
                .block();
    }


}
