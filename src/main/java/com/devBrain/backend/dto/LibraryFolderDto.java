package com.devBrain.backend.dto;

import java.util.List;

public record LibraryFolderDto(
        String folder,
        List<LibraryItemCardDto> files
) {}
