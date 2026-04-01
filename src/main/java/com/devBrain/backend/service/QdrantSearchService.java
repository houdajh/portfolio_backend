package com.devBrain.backend.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QdrantSearchService {

    private final VectorStore vectorStore;

    public QdrantSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void index(List<Document> documents) {
        int batchSize = 20;
        for (int i = 0; i < documents.size(); i += batchSize) {
            int end = Math.min(i + batchSize, documents.size());
            System.out.println("INDEXING DOC: " + documents.get(i).getText().substring(0, 50));
            vectorStore.add(documents.subList(i, end));
        }
    }


    public List<Document> search(String query, int topK) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .build()
        );
    }

}

