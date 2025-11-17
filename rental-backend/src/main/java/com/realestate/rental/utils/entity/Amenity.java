package com.realestate.rental.utils.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.realestate.rental.utils.enumeration.AmenityCategory;

import java.util.UUID;

@Entity
@Table(name = "amenities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "name_fr", length = 100)
    private String nameFr;

    @Column(length = 50)
    private String icon;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private AmenityCategory category;
}

