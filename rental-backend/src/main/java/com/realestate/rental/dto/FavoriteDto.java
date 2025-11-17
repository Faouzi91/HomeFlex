package com.realestate.rental.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FavoriteDto {
    private UUID id;
    private UUID userId;
    private UUID propertyId;
    private PropertyDto property;
    private LocalDateTime createdAt;
}