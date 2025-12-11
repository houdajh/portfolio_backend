package com.devBrain.backend.service;

import com.devBrain.backend.model.Profile;
import com.devBrain.backend.repository.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final ProfileRepository repo;

    public ProfileService(ProfileRepository repo) {
        this.repo = repo;
    }

    public Profile getProfile() {
        return repo.findById(1L)
                   .orElseThrow(() -> new RuntimeException("Profile not found"));
    }
}
