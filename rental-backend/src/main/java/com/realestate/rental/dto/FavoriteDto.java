package com.realestate.rental.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FavoriteDto(
        UUID id,
        UUID userId,
        UUID propertyId,
        PropertyDto property,
        LocalDateTime createdAt
) {}