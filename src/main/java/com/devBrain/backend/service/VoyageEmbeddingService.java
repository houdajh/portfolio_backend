package com.devBrain.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class VoyageEmbeddingService {

    private final WebClient client;

    public VoyageEmbeddingService(WebClient.Builder builder) {
        this.client = builder
                .baseUrl("https://api.voyageai.com/v1")
                .defaultHeader("Authorization", "Bearer " + System.getenv("VOYAGE_API_KEY"))
                .build();
    }

    public float[] embed(String text) {

        int retries = 3;

        for (int attempt = 1; attempt <= retries; attempt++) {
            try {
                Map response = client.post()
                        .uri("/embeddings")
                        .bodyValue(Map.of(
                                "model", "voyage-2",
                                "input", List.of(text)
                        ))
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                List<Number> vector =
                        (List<Number>) ((Map) ((List) response.get("data")).get(0)).get("embedding");

                float[] arr = new float[vector.size()];
                for (int i = 0; i < vector.size(); i++) {
                    arr[i] = vector.get(i).floatValue();
                }
                return arr;

            } catch (org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests e) {

                if (attempt == retries) {
                    throw new IllegalStateException("VoyageAI rate limit exceeded after retries", e);
                }

                try {
                    // 🔴 Backoff (Voyage-safe)
                    Thread.sleep(3000L * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Embedding retry interrupted", ie);
                }
            }
        }

        throw new IllegalStateException("Unreachable");
    }

}
