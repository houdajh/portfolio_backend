package com.portfolio.backend.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QdrantSearchService {

    private final VectorStore vectorStore;

    public QdrantSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Indexe un document texte avec des métadonnées.
     */
    public void index(String content, Map<String, Object> metadata) {
        Document doc = new Document(content, metadata);
        vectorStore.add(List.of(doc));
    }

    /**
     * Overload simple avec topK par défaut.
     */
    public List<Document> search(String query) {
        return search(query, 5);
    }

    /**
     * Semantic search avec topK paramétrable.
     */
    public List<Document> search(String query, int topK) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();

        return vectorStore.similaritySearch(request);
    }
}

