package com.devBrain.backend.model;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String slug;
    private String title;

    @Column(length = 2000)
    private String shortDescription;

    @Column(length = 8000)
    private String details;

    private String tags;

    private String githubUrl;
    private String demoUrl;

    private LocalDate startDate;
    private LocalDate endDate; // null if still ongoing
}