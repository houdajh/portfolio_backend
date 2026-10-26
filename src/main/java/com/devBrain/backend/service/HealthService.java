package com.devBrain.backend.service;

import com.devBrain.backend.dto.HealthResponseDto;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private final String appName;
    private final ObjectProvider<BuildProperties> buildProperties;
    private final boolean ragEnabled;

    public HealthService(@Value("${spring.application.name}") String appName,
                         ObjectProvider<BuildProperties> buildProperties,
                         @Value("${app.rag.enabled:false}") boolean ragEnabled) {
        this.appName = appName;
        this.buildProperties = buildProperties;
        this.ragEnabled = ragEnabled;
    }

    public HealthResponseDto status() {
        BuildProperties build = buildProperties.getIfAvailable();
        String version = build != null ? build.getVersion() : "unknown";
        return new HealthResponseDto(appName, version, ragEnabled);
    }
}
