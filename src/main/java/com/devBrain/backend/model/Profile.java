package com.devBrain.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    private Long id;

    private String name;
    private String role;

    @Column(length = 2000)
    private String summary;

    private String skills;

    @Column(length = 500)
    private String quote;

    private String cvUrl;
}
