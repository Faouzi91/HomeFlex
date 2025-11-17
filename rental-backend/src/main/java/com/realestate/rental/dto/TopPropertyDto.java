package com.realestate.rental.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TopPropertyDto {
    private UUID id;
    private String title;
    private String city;
    private Integer viewCount;
    private Integer favoriteCount;
}
