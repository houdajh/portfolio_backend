package com.devBrain.backend.dto;

public record LibraryItemCardDto(
        String title,
        String category,
        String url,
        String tags,
        boolean usedInRag
) {}
