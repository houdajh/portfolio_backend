package com.devBrain.backend.config;


import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;

@Configuration
public class QdrantConfig {

    @Bean
    public QdrantVectorStore qdrantVectorStore(
            @Value("${spring.ai.vectorstore.qdrant.url}") String url,
            @Value("${spring.ai.vectorstore.qdrant.api-key}") String apiKey,
            @Value("${spring.ai.vectorstore.qdrant.collection-name}") String collectionName,
            EmbeddingModel embeddingModel
    ) {

        String host = url
                .replace("https://", "")
                .replace("http://", "");

        QdrantClient client = new QdrantClient(
                QdrantGrpcClient.newBuilder(host, 6334, true)
                        .withApiKey(apiKey)
                        .build()
        );

        return QdrantVectorStore.builder(client, embeddingModel)
                .collectionName(collectionName)
                .build();
    }
}