package com.devBrain.backend.controller;

import com.devBrain.backend.dto.LibraryItemCardDto;
import com.devBrain.backend.dto.LibraryItemDetailDto;
import com.devBrain.backend.service.LibraryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService service;

    public LibraryController(LibraryService service) {
        this.service = service;
    }

    @GetMapping
    public List<LibraryItemCardDto> getLibrary() {
        return service.getAllItems();
    }

    @GetMapping("/{id}")
    public LibraryItemDetailDto getLibraryItem(@PathVariable Long id) {
        return service.getItem(id);
    }
}
