package com.devBrain.backend.controller;

import com.devBrain.backend.dto.HealthResponseDto;
import com.devBrain.backend.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class ApiHealthController {

    private final HealthService healthService;

    public ApiHealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public HealthResponseDto health() {
        return healthService.status();
    }
}
