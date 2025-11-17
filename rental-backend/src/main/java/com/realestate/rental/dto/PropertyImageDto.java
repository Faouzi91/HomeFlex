package com.realestate.rental.dto;

import lombok.Data;

import java.util.UUID;

// PropertyImageDto.java
@Data
public class PropertyImageDto {
    private UUID id;
    private String imageUrl;
    private String thumbnailUrl;
    private Integer displayOrder;
    private Boolean isPrimary;
}
