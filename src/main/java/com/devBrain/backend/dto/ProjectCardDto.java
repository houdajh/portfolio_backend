package com.devBrain.backend.dto;

public record ProjectCardDto(
        Long id,
        String slug,
        String title,
        String shortDescription,
        String tags,
        String githubUrl,
        String demoUrl,
        String status
) { }
