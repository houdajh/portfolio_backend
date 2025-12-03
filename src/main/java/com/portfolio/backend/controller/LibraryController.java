package com.portfolio.backend.controller;

import com.portfolio.backend.dto.LibraryItemCardDto;
import com.portfolio.backend.dto.LibraryItemDetailDto;
import com.portfolio.backend.service.LibraryService;
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
