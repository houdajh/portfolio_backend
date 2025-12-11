package com.devBrain.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubContentService {

    @Value("${app.library.github.owner}")
    private String owner;

    @Value("${app.library.github.repo}")
    private String repo;

    @Value("${app.library.github.branch}")
    private String branch;

    @Value("${app.library.github.base-path}")
    private String basePath; // = "library"

    @Value("${app.library.github.token:}")
    private String token;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();


    /**
     * Retourne TOUS les fichiers .md dans :
     *  - library/md/...
     *  - portfolio/...
     */
    public List<GitHubFile> listAllMarkdownFiles() {
        List<GitHubFile> list = new ArrayList<>();

        // 1. Indexer "library/md"
        list.addAll(fetchRecursive(basePath + "/md"));

        // 2. Indexer "portfolio"
        list.addAll(fetchRecursive("portfolio"));

        return list;
    }


    /**
     * Récupère récursivement tous les .md dans un dossier GitHub.
     */
    private List<GitHubFile> fetchRecursive(String path) {
        List<GitHubFile> results = new ArrayList<>();

        String url = "https://api.github.com/repos/%s/%s/contents/%s?ref=%s"
                .formatted(owner, repo, path, branch);

        try {
            HttpRequest.Builder req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/vnd.github+json");

            if (!token.isBlank()) {
                req.header("Authorization", "Bearer " + token);
            }

            String json = client.send(req.build(), HttpResponse.BodyHandlers.ofString()).body();
            JsonNode nodes = mapper.readTree(json);

            for (JsonNode n : nodes) {
                String type = n.get("type").asText();
                String name = n.get("name").asText();
                String filePath = n.get("path").asText();

                if ("dir".equals(type)) {
                    results.addAll(fetchRecursive(filePath));
                }

                if ("file".equals(type) && name.endsWith(".md")) {
                    results.add(new GitHubFile(
                            name,
                            filePath,
                            n.get("download_url").asText()
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("GitHub read failed for path: " + path, e);
        }

        return results;
    }


    public String download(GitHubFile file) {
        try {
            HttpRequest.Builder req = HttpRequest.newBuilder()
                    .uri(URI.create(file.downloadUrl()));

            if (!token.isBlank()) {
                req.header("Authorization", "Bearer " + token);
            }

            return client.send(req.build(), HttpResponse.BodyHandlers.ofString()).body();

        } catch (Exception e) {
            throw new RuntimeException("Failed to download: " + file.path(), e);
        }
    }


    public record GitHubFile(String name, String path, String downloadUrl) {}
}
