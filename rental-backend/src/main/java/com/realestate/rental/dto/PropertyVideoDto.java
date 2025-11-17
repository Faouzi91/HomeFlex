package com.realestate.rental.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PropertyVideoDto {
    private UUID id;
    private String videoUrl;
    private String thumbnailUrl;
    private Integer durationSeconds;
}
