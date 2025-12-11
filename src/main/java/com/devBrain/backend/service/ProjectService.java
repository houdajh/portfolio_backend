package com.devBrain.backend.service;

import com.devBrain.backend.dto.ProjectCardDto;
import com.devBrain.backend.dto.ProjectDetailDto;
import com.devBrain.backend.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public List<ProjectCardDto> getAllProjects() {
        return repository.findAllByOrderByStartDateDesc()
                .stream()
                .map(p -> new ProjectCardDto(
                        p.getId(),
                        p.getSlug(),
                        p.getTitle(),
                        p.getShortDescription(),
                        p.getTags(),
                        p.getStartDate(),
                        p.getEndDate()
                )).toList();
    }

    public ProjectDetailDto getProjectBySlug(String slug) {
        var p = repository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return new ProjectDetailDto(
                p.getId(),
                p.getSlug(),
                p.getTitle(),
                p.getShortDescription(),
                p.getDetails(),
                p.getTags(),
                p.getGithubUrl(),
                p.getDemoUrl()
        );
    }
}
