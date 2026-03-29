package com.homeflex.features.property.dto.response;

import com.homeflex.core.dto.response.UserDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingDto(
        UUID id,
        PropertyDto property,
        UserDto tenant,
        String bookingType,
        LocalDateTime requestedDate,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        String message,
        Integer numberOfOccupants,
        String landlordResponse,
        LocalDateTime respondedAt,
        LocalDateTime createdAt
) {}
