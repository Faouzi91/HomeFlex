package com.homeflex.features.vehicle.dto.response;

import com.homeflex.features.vehicle.domain.enums.VehicleBookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record VehicleBookingResponse(
        UUID id,
        UUID vehicleId,
        UUID tenantId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalPrice,
        String currency,
        VehicleBookingStatus status,
        BigDecimal platformFee,
        String message,
        LocalDateTime createdAt
) {}
