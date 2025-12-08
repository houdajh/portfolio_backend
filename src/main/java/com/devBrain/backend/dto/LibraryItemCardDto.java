package com.devBrain.backend.dto;

public record LibraryItemCardDto(
        Long id,
        String category,
        String title,
        String summary,
        String tags
) { }