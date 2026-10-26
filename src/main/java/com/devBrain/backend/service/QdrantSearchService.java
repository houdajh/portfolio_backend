package com.devBrain.backend.service;

import org.springframework.ai.document.Document;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QdrantSearchService {

    // Resolved lazily: the VectorStore bean contacts Qdrant when it is created,
    // so injecting it directly would fail the whole request chain when Qdrant is down.
    private final ObjectProvider<VectorStore> vectorStoreProvider;

    public QdrantSearchService(ObjectProvider<VectorStore> vectorStoreProvider) {
        this.vectorStoreProvider = vectorStoreProvider;
    }

    public void index(List<Document> documents) {
        VectorStore vectorStore = vectorStoreProvider.getObject();
        int batchSize = 20;
        for (int i = 0; i < documents.size(); i += batchSize) {
            int end = Math.min(i + batchSize, documents.size());
            System.out.println("INDEXING DOC: " + documents.get(i).getText().substring(0, 50));
            vectorStore.add(documents.subList(i, end));
        }
    }


    public List<Document> search(String query, int topK) {
        try {
            return vectorStoreProvider.getObject().similaritySearch(
                    SearchRequest.builder()
                            .query(query)
                            .topK(topK)
                            .build()
            );
        } catch (RuntimeException e) {
            throw new VectorStoreUnavailableException("Vector search failed", e);
        }
    }

}

