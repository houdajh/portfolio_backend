package com.devBrain.backend.repository;

import com.devBrain.backend.model.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {
}
