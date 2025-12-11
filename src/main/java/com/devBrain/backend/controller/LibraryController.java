// src/main/java/com/devBrain/backend/controller/LibraryController.java
package com.devBrain.backend.controller;

import com.devBrain.backend.dto.LibraryFolderDto;
import com.devBrain.backend.dto.LibraryItemCardDto;
import com.devBrain.backend.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final LibraryService service;

    public LibraryController(LibraryService service) {
        this.service = service;
    }

    @GetMapping("/list")
    public List<LibraryFolderDto> list() {
        return service.listFolders();
    }



    // Optionnel : exposer un endpoint pour un fichier markdown spécifique
    @GetMapping("/md/{fileName}")
    public ResponseEntity<String> getMarkdown(@PathVariable String fileName) {
        String content = service.loadMarkdownRaw(fileName);
        return ResponseEntity.ok(content);
    }
}
