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

    public List<LibraryFolderDto> listFolders() {
        try {
            List<LibraryFolderDto> folders = new ArrayList<>();

            List<String> folderNames = fetchFoldersFromGitHub();

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
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET();

        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token.trim());
        }

        HttpResponse<String> response =
                httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(
                    "GitHub API call failed. Status: " + response.statusCode() + ", body: " + response.body()
            );
        }

        JsonNode json = objectMapper.readTree(response.body());

        if (!json.isArray()) {
            throw new IllegalStateException(
                    "Unexpected GitHub API response. Expected JSON array but got: " + json.getNodeType()
            );
        }

        List<String> folders = new ArrayList<>();
        for (JsonNode node : json) {
            String type = node.path("type").asText();
            String name = node.path("name").asText();

            if ("dir".equals(type) && !name.isBlank()) {
                folders.add(name);
            }
        }

        return folders;
    }

    private List<LibraryItemCardDto> fetchFilesInFolder(String folder) throws Exception {
        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo
                + "/contents/" + basePath + "/pdf/" + encodePathPart(folder) + "?ref=" + branch;

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET();

        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token.trim());
        }

        HttpResponse<String> response =
                httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(
                    "GitHub API call failed. Status: " + response.statusCode() + ", body: " + response.body()
            );
        }

        JsonNode json = objectMapper.readTree(response.body());

        if (!json.isArray()) {
            throw new IllegalStateException(
                    "Unexpected GitHub API response. Expected JSON array but got: " + json.getNodeType()
            );
        }

        List<LibraryItemCardDto> items = new ArrayList<>();

        for (JsonNode node : json) {
            String type = node.path("type").asText();
            String name = node.path("name").asText();
            String downloadUrl = node.path("download_url").asText();

            if (!"file".equals(type)) {
                continue;
            }

            if (!name.toLowerCase().endsWith(".pdf")) {
                continue;
            }

            if (downloadUrl == null || downloadUrl.isBlank()) {
                continue;
            }

            items.add(new LibraryItemCardDto(
                    name.replace(".pdf", ""),
                    folder,
                    downloadUrl,
                    folder,
                    false
            ));
        }

        return items;
    }

    private List<LibraryItemCardDto> fetchFromGitHubFolder(String subFolder, boolean usedInRag) throws Exception {
        String apiUrl = "https://api.github.com/repos/" + owner + "/" + repo
                + "/contents/" + basePath + "/" + subFolder
                + "?ref=" + branch;

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .GET();

        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token.trim());
        }

        HttpResponse<String> response = httpClient.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("GitHub API error " + response.statusCode() + " for " + apiUrl);
        }

        JsonNode json = objectMapper.readTree(response.body());
        List<LibraryItemCardDto> items = new ArrayList<>();

        if (!json.isArray()) {
            return items;
        }

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
            String tags = category;

            items.add(new LibraryItemCardDto(
                    title,
                    category,
                    downloadUrl,
                    tags,
                    usedInRag
            ));
        }

        return items;
    }

    public String loadMarkdownRaw(String fileName) {
        try {
            String rawUrl = "https://raw.githubusercontent.com/" + owner + "/" + repo + "/" + branch
                    + "/" + basePath + "/md/" + encodePathPart(fileName);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(rawUrl))
                    .GET();

            if (token != null && !token.isBlank()) {
                builder.header("Authorization", "Bearer " + token.trim());
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

    private String encodePathPart(String s) {
        return s.replace(" ", "%20");
    }

    private String guessCategoryFromPath(String subFolder, String title) {
        if (title.toLowerCase().contains("spring") || title.toLowerCase().contains("java")) {
            return "Backend";
        }
        if ("md".equals(subFolder)) {
            return "Notes";
        }
        return "Library";
    }
}