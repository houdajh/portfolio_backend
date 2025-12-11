package com.devBrain.backend.controller;

import com.devBrain.backend.dto.ProjectCardDto;
import com.devBrain.backend.dto.ProjectDetailDto;
import com.devBrain.backend.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProjectCardDto> getProjects() {
        return service.getAllProjects();
    }

    @GetMapping("/{slug}")
    public ProjectDetailDto getProject(@PathVariable String slug) {
        return service.getProjectBySlug(slug);
    }
}
