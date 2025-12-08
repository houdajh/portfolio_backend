package com.devBrain.backend.dto;

public record ProjectCardDto(
        Long id,
        String slug,
        String title,
        String highlight,
        String shortDescription,
        String tags
) { }
