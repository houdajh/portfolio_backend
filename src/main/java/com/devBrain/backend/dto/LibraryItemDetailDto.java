package com.devBrain.backend.dto;

public record LibraryItemDetailDto(
        Long id,
        String category,
        String title,
        String summary,
        String content,
        String tags
) { }