package com.devBrain.backend.service;

import com.devBrain.backend.dto.TimelineEntryDto;
import com.devBrain.backend.repository.TimelineEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimelineService {

    private final TimelineEntryRepository repository;

    public TimelineService(TimelineEntryRepository repository) {
        this.repository = repository;
    }

    public List<TimelineEntryDto> getTimeline() {
        return repository.findAllByOrderBySortOrderDesc()
                .stream()
                .map(e -> new TimelineEntryDto(
                        e.getId(),
                        e.getYear(),
                        e.getTitle(),
                        e.getDescription()
                ))
                .toList();
    }
}
