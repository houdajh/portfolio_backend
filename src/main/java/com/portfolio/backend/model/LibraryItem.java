package com.portfolio.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "library_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibraryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;        // "Backend", "Frontend", "Architecture", "AI"
    private String title;           // "DTO vs Entity — Best Practices"
    private String tags;            // "spring,architecture,used-in-rag"

    @Column(length = 2000)
    private String summary;         // ce qui apparait dans la liste

    @Column(length = 12000)
    private String content;         // texte complet pour le popup

    private String url;
}
