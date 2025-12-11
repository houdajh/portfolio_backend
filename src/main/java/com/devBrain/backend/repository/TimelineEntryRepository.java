package com.devBrain.backend.repository;

import com.devBrain.backend.model.TimelineEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimelineEntryRepository extends JpaRepository<TimelineEntry, Long> {

    List<TimelineEntry> findAllByOrderBySortOrderDesc();
}
