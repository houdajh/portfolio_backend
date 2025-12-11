package com.devBrain.backend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class GithubClient {

    private final GithubProperties props;
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public GithubClient(GithubProperties props) {
        this.props = props;
    }

    public JsonNode listFiles(String folder) {
        try {
            String apiUrl = "https://api.github.com/repos/" +
                    props.getOwner() + "/" +
                    props.getRepo() + "/contents/" +
                    props.getBasePath() + "/" + folder +
                    "?ref=" + props.getBranch();

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET();

            if (props.getToken() != null && !props.getToken().isBlank()) {
                builder.header("Authorization", "Bearer " + props.getToken());
            }

            HttpResponse<String> response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400)
                throw new RuntimeException("GitHub API error : " + response.statusCode());

            return mapper.readTree(response.body());

        } catch (Exception e) {
            throw new RuntimeException("Failed to list files on GitHub", e);
        }
    }

    public byte[] download(String downloadUrl) {
        try (InputStream in = new URL(downloadUrl).openStream()) {
            return in.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed downloading file: " + downloadUrl, e);
        }
    }
}
