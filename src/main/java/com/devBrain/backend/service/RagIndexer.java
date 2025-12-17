package com.devBrain.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
@RequiredArgsConstructor
public class RagIndexer {

    private final QdrantSearchService qdrant;
    private final GitHubContentService github;

    public void reindex() {

        var mdFiles = github.listAllMarkdownFiles();

        int count = 0;

        for (var f : mdFiles) {
            String content = github.download(f);

            qdrant.index(content, Map.of(
                    "filename", f.name(),
                    "path", f.path(),
                    "source", "github"
            ));

            count++;
            System.out.println("Indexed " + count + "/" + mdFiles.size() + " : " + f.path());

            // 🔴 HARD throttle — Voyage safe
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Reindex interrupted", e);
            }
        }
    }
}
