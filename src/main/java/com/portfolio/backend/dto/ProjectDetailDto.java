package com.portfolio.backend.dto;

public record ProjectDetailDto(
        Long id,
        String slug,
        String title,
        String highlight,
        String shortDescription,
        String details,
        String tags,
        String githubUrl,
        String demoUrl
) { }