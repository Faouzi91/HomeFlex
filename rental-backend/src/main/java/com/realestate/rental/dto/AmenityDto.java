package com.realestate.rental.dto;

import lombok.Data;

import java.util.UUID;

// AmenityDto.java
@Data
public class AmenityDto {
    private UUID id;
    private String name;
    private String nameFr;
    private String icon;
    private String category;
}
