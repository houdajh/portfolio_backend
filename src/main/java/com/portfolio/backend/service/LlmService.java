package com.portfolio.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    @Value("${groq.api-key}")
    private String apiKey;

    private final RestTemplate client = new RestTemplate();

    public String askGroq(String question, String context) {

        Map<String, Object> body = Map.of(
                "model", "llama-3.1-8b-instant",
                "messages", List.of(
                        Map.of("role", "system", "content",
                                "You are Houda Jouhar. Always answer AS Houda, using first person. " +
                                        "Use the provided context to answer. " +
                                        "ALWAYS answer in the same language as the user’s question. " +
                                        "If user speaks English → answer in English. " +
                                        "If user speaks French → answer in French. " +
                                        "If user speaks Arabic → answer in Arabic, etc."
                        ),
                        Map.of("role", "user", "content",
                                "Context:\n" + context + "\n\n" +
                                        "Question:\n" + question)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map response = client.postForObject(
                "https://api.groq.com/openai/v1/chat/completions",
                new HttpEntity<>(body, headers),
                Map.class
        );

        List choices = (List) response.get("choices");
        Map choice = (Map) choices.get(0);
        Map message = (Map) choice.get("message");

        return (String) message.get("content");
    }

}
