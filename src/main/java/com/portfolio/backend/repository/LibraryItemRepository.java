package com.portfolio.backend.repository;

import com.portfolio.backend.model.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryItemRepository extends JpaRepository<LibraryItem, Long> {
}
