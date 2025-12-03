package com.portfolio.backend.dto;

public record TimelineEntryDto(
        Long id,
        int year,
        String title,
        String description
) { }
