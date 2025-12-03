package com.portfolio.backend.dto;

public record LibraryItemCardDto(
        Long id,
        String category,
        String title,
        String summary,
        String tags
) { }