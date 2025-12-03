package com.portfolio.backend.controller;

import com.portfolio.backend.dto.ProjectCardDto;
import com.portfolio.backend.dto.ProjectDetailDto;
import com.portfolio.backend.service.ProjectService;
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
