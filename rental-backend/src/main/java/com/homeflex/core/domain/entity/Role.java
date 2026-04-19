package com.homeflex.core.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Convention: ROLE_TENANT, ROLE_LANDLORD, ROLE_ADMIN, ROLE_MONITORING
    // Spring Security's hasRole('TENANT') checks for "ROLE_TENANT" automatically.
    @Column(unique = true, nullable = false, length = 40)
    private String name;

    @Column(length = 255)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
