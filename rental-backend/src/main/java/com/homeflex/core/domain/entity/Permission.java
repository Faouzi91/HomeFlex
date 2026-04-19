package com.homeflex.core.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 60)
    private String name;

    @Column(length = 255)
    private String description;

    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
