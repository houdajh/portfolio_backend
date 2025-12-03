package com.portfolio.backend.controller;

import com.portfolio.backend.dto.TimelineEntryDto;
import com.portfolio.backend.service.TimelineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeline")
public class TimelineController {

    private final TimelineService service;

    public TimelineController(TimelineService service) {
        this.service = service;
    }

    @GetMapping
    public List<TimelineEntryDto> getTimeline() {
        return service.getTimeline();
    }
}
