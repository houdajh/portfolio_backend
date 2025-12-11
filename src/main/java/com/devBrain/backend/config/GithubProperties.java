package com.devBrain.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.library.github")
public class GithubProperties {

    private String owner;
    private String repo;
    private String branch;
    private String basePath;
    private String token;
}
