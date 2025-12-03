package com.portfolio.backend.model;



import jakarta.persistence.*;
import lombok.*;

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
    private String highlight;

    @Column(length = 2000)
    private String shortDescription;

    @Column(length = 8000)
    private String details;

    private String tags;

    private String githubUrl;
    private String demoUrl;
}