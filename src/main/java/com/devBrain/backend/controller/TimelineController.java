package com.devBrain.backend.controller;

import com.devBrain.backend.dto.TimelineEntryDto;
import com.devBrain.backend.service.TimelineService;
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
