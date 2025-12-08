package com.devBrain.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RagIndexer {

    private final QdrantSearchService qdrant;

    public void reindex() throws IOException {
        Path folder = Paths.get("rag-data");

        try (var files = Files.list(folder)) {
            files.filter(f -> f.toString().endsWith(".md"))
                 .forEach(f -> {
                     try {
                         String content = Files.readString(f);
                         Map<String, Object> metadata = Map.of(
                                 "filename", f.getFileName().toString()
                         );

                         qdrant.index(content, metadata);
                     } catch (Exception e) {
                         throw new RuntimeException(e);
                     }
                 });
        }
    }
}
