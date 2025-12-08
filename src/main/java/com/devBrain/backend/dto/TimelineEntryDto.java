package com.devBrain.backend.dto;

public record TimelineEntryDto(
        Long id,
        int year,
        String title,
        String description
) { }
