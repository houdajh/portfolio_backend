// src/main/java/com/devBrain/backend/service/LibraryService.java
package com.devBrain.backend.service;

import com.devBrain.backend.dto.LibraryFolderDto;
import com.devBrain.backend.dto.LibraryItemCardDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class LibraryService {

    private final String owner;
    private final String repo;
    private final String branch;
    private final String basePath;
    private final String token;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LibraryService(
            @Value("${app.library.github.owner}") String owner,
            @Value("${app.library.github.repo}") String repo,
            @Value("${app.library.github.branch}") String branch,
            @Value("${app.library.github.base-path}") String basePath,
            @Value("${app.library.github.token:}") String token
    ) {
        this.owner = owner;
        this.repo = repo;
        this.branch = branch;
        this.basePath = basePath.replaceFirst("^/+", "").replaceAll("/+$", "");
        this.token = token;
    }

    // =============== LIST ===============

    public List<LibraryItemCardDto> listItems() {
        try {
            List<LibraryItemCardDto> result = new ArrayList<>();

            result.addAll(fetchFromGitHubFolder("pdf", false));


            return result;
        } catch (Exception e) {
            throw new RuntimeException("Unable to list library items from GitHub", e);
        }
    }

    private List<LibraryItemCardDto> fetchFromGitHubFolder(String subFolder, boolean usedInRag) throws Exception {
        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo
                + "/contents/" + basePath + "/" + subFolder
                + "?ref=" + branch;

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET();

        if (!token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }

        HttpResponse<String> response = httpClient.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() >= 400) {
            throw new RuntimeException("GitHub API error " + response.statusCode() + " for " + apiUrl);
        }

        JsonNode json = objectMapper.readTree(response.body());
        List<LibraryItemCardDto> items = new ArrayList<>();

        for (JsonNode node : json) {
            if (!node.has("type") || !node.has("name") || !node.has("download_url")) {
                continue;
            }

            if (!"file".equals(node.get("type").asText())) {
                continue;
            }

            String name = node.get("name").asText();
            String downloadUrl = node.get("download_url").asText();

            String title = name.replace(".pdf", "").replace(".md", "");
            String category = guessCategoryFromPath(subFolder, title);
            String tags = category; // simpliste, tu pourras améliorer

            items.add(new LibraryItemCardDto(
                    title,
                    category,
                    downloadUrl,  // <--- FRONT utilisera directement cette URL
                    tags,
                    usedInRag
            ));
        }

        return items;
    }

    // =============== DETAIL (Markdown) ===============

    public String loadMarkdownRaw(String fileName) {
        try {
            String rawUrl = "https://raw.githubusercontent.com/" + owner + "/" + repo + "/" + branch
                    + "/" + basePath + "/md/" + encodePathPart(fileName);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(rawUrl))
                    .GET();

            if (!token.isBlank()) {
                builder.header("Authorization", "Bearer " + token);
            }

            HttpResponse<String> response = httpClient.send(
                    builder.build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            if (response.statusCode() >= 400) {
                throw new RuntimeException("GitHub RAW error " + response.statusCode() + " for " + rawUrl);
            }

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Unable to load markdown from GitHub", e);
        }
    }

    // =============== UTILS ===============

    private String encodePathPart(String s) {
        return s.replace(" ", "%20");
    }

    private String guessCategoryFromPath(String subFolder, String title) {
        // Tu pourras faire mieux plus tard (backend, threads, design pattern, etc.)
        if (title.toLowerCase().contains("spring") || title.toLowerCase().contains("java")) {
            return "Backend";
        }
        if (subFolder.equals("md")) {
            return "Notes";
        }
        return "Library";
    }

    public List<LibraryFolderDto> listFolders() {
        try {
            List<LibraryFolderDto> folders = new ArrayList<>();

            // 1. read list of folder names in /library/pdf
            List<String> folderNames = fetchFoldersFromGitHub();

            // 2. for each folder, list files inside
            for (String folder : folderNames) {
                List<LibraryItemCardDto> files = fetchFilesInFolder(folder);
                folders.add(new LibraryFolderDto(folder, files));
            }

            return folders;
        } catch (Exception e) {
            throw new RuntimeException("Unable to read PDF folders from GitHub", e);
        }
    }

    private List<String> fetchFoldersFromGitHub() throws Exception {
        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo
                + "/contents/" + basePath + "/pdf?ref=" + branch;

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET();

        if (!token.isBlank()) builder.header("Authorization", "Bearer " + token);

        HttpResponse<String> response =
                httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        JsonNode json = objectMapper.readTree(response.body());

        List<String> folders = new ArrayList<>();
        for (JsonNode node : json) {
            if (!node.has("type") || !node.has("name")) {
                continue;
            }

            if ("dir".equals(node.get("type").asText())) {
                folders.add(node.get("name").asText());
            }
        }
        return folders;
    }

    private List<LibraryItemCardDto> fetchFilesInFolder(String folder) throws Exception {

        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo
                + "/contents/" + basePath + "/pdf?ref=" + branch;

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET();

        if (!token.isBlank()) builder.header("Authorization", "Bearer " + token);

        HttpResponse<String> response =
                httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        JsonNode json = objectMapper.readTree(response.body());
        List<LibraryItemCardDto> items = new ArrayList<>();

        for (JsonNode node : json) {
            if (!node.has("name") || !node.has("download_url")) {
                continue;
            }

            String name = node.get("name").asText();

            if (!name.endsWith(".pdf")) continue;

            items.add(new LibraryItemCardDto(
                    node.get("name").asText().replace(".pdf", ""),
                    folder,
                    node.get("download_url").asText(),
                    folder,
                    false
            ));
        }

        return items;
    }

}
