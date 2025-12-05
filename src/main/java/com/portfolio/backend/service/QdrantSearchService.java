package com.portfolio.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
@Service
public class QdrantSearchService {

    @Value("${qdrant.url}")
    private String qdrantUrl;

    @Value("${qdrant.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, Object>> search(float[] embedding) {
        Map<String, Object> request = Map.of(
                "vector", embedding,
                "limit", 5,
                "with_payload", true
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        String url = qdrantUrl + "/collections/portfolio_docs/points/search";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        return (List<Map<String, Object>>) response.getBody().get("result");
    }
}
