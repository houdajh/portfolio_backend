package com.devBrain.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RagIndexer {

    private final QdrantSearchService qdrant;
    private final GitHubContentService github;
    private volatile boolean indexed = false;

    public synchronized  void reindex() {
        if (indexed) return;

        var mdFiles = github.listAllMarkdownFiles();

        for (var f : mdFiles) {

            String content = github.download(f);

            Map<String, Object> metadata = Map.of(
                    "filename", f.name(),
                    "path", f.path(),
                    "source", "github"
            );

            List<Document> chunks = chunk(content, metadata);

            qdrant.index(chunks);
        }
        indexed = true;
    }

    private List<Document> chunk(String content, Map<String, Object> metadata) {

        int chunkSize = 1200;
        int overlap = 200;

        List<Document> chunks = new java.util.ArrayList<>();

        for (int start = 0; start < content.length(); start += (chunkSize - overlap)) {
            int end = Math.min(start + chunkSize, content.length());
            chunks.add(new Document(content.substring(start, end), metadata));
        }

        return chunks;
    }
}
