package com.devBrain.backend.dto;

import java.time.LocalDate;

public record ProjectCardDto(
        Long id,
        String slug,
        String title,
        String shortDescription,
        String tags,
        LocalDate startDate,
        LocalDate endDate
) { }
