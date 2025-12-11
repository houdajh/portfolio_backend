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

        for (var f : mdFiles) {
            String content = github.download(f);

            qdrant.index(content, Map.of(
                    "filename", f.name(),
                    "path", f.path(),
                    "source", "github"
            ));
        }
    }
}
