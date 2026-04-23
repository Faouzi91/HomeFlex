package com.homeflex.features.property.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingCreateRequest(
        @NotNull UUID propertyId,
        @NotBlank String bookingType,
        LocalDateTime requestedDate,
        LocalDate startDate,
        LocalDate endDate,
        String message,
        Integer numberOfOccupants,
        String idempotencyKey
) {}
