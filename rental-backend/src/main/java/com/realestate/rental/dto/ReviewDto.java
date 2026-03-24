package com.realestate.rental.dto;

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
