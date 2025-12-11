package com.devBrain.backend.service;

import com.devBrain.backend.config.GithubProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GithubService {

    private final GithubProperties props;
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public GithubService(GithubProperties props) {
        this.props = props;
    }

    /**
     * Downloads any file inside the basePath folder (library/ or portfolio/)
     * using the GitHub API — same method as LibraryService.
     */
    public byte[] downloadFromGithub(String fileName) {
        try {
            String apiUrl =
                    "https://api.github.com/repos/" + props.getOwner() + "/" + props.getRepo()
                            + "/contents/" + props.getBasePath()
                            + "?ref=" + props.getBranch();

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET();

            if (props.getToken() != null && !props.getToken().isBlank()) {
                builder.header("Authorization", "Bearer " + props.getToken());
            }

            HttpResponse<String> response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                throw new RuntimeException("GitHub API error " + response.statusCode() + " for " + apiUrl);
            }

            JsonNode files = mapper.readTree(response.body());

            // Find CV inside the folder
            for (JsonNode file : files) {
                if (file.get("name").asText().equals(fileName)) {

                    // GitHub already provides the correct RAW link
                    String downloadUrl = file.get("download_url").asText();

                    try (InputStream in = new URL(downloadUrl).openStream()) {
                        return in.readAllBytes();
                    }
                }
            }

            throw new RuntimeException("CV not found in GitHub folder: " + props.getBasePath());

        } catch (Exception e) {
            throw new RuntimeException("Failed to download CV from GitHub", e);
        }
    }
}
