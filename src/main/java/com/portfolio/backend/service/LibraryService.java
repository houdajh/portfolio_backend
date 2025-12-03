package com.portfolio.backend.service;

import com.portfolio.backend.dto.LibraryItemCardDto;
import com.portfolio.backend.dto.LibraryItemDetailDto;
import com.portfolio.backend.repository.LibraryItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryService {

    private final LibraryItemRepository repository;

    public LibraryService(LibraryItemRepository repository) {
        this.repository = repository;
    }

    public List<LibraryItemCardDto> getAllItems() {
        return repository.findAll().stream()
                .map(i -> new LibraryItemCardDto(
                        i.getId(),
                        i.getCategory(),
                        i.getTitle(),
                        i.getSummary(),
                        i.getTags()
                ))
                .toList();
    }

    public LibraryItemDetailDto getItem(Long id) {
        var i = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Library item not found"));

        return new LibraryItemDetailDto(
                i.getId(),
                i.getCategory(),
                i.getTitle(),
                i.getSummary(),
                i.getContent(),
                i.getTags()
        );
    }
}
