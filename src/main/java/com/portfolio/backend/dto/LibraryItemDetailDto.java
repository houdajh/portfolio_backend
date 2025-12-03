package com.portfolio.backend.dto;

public record LibraryItemDetailDto(
        Long id,
        String category,
        String title,
        String summary,
        String content,
        String tags
) { }