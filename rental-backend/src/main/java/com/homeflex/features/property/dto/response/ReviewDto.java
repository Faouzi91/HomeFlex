package com.homeflex.features.property.dto.response;

import com.homeflex.core.dto.response.UserDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewDto(
        UUID id,
        UUID propertyId,
        UserDto reviewer,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {}
