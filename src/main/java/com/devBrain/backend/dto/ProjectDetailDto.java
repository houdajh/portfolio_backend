package com.devBrain.backend.dto;

public record ProjectDetailDto(
        Long id,
        String slug,
        String title,
        String shortDescription,
        String details,
        String tags,
        String githubUrl,
        String demoUrl
) { }