package com.portfolio.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "timeline_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimelineEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int year;
    private String title;

    @Column(length = 2000)
    private String description;

    private int sortOrder; // pour l'ordre d'affichage
}
