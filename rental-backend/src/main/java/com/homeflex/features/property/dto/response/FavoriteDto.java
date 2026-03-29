package com.homeflex.features.property.dto.response;

import com.homeflex.core.dto.response.UserDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FavoriteDto(
        UUID id,
        UUID userId,
        UUID propertyId,
        PropertyDto property,
        LocalDateTime createdAt
) {}