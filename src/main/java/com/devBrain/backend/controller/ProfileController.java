package com.devBrain.backend.controller;

import com.devBrain.backend.model.Profile;
import com.devBrain.backend.service.GithubService;
import com.devBrain.backend.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final GithubService githubService;

    public ProfileController(ProfileService profileService, GithubService githubService) {
        this.profileService = profileService;
        this.githubService = githubService;
    }

    @GetMapping
    public Profile getProfile() {
        return profileService.getProfile();
    }

    @GetMapping("/cv")
    public ResponseEntity<byte[]> downloadCv() {
        byte[] pdf = githubService.downloadFromGithub("Jouhar_Houda_CV_English.pdf");

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition",
                        "attachment; filename=Houda_Jouhar_CV.pdf")
                .body(pdf);
    }



}
