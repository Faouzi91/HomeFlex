package com.homeflex.features.vehicle.dto.response;

import com.homeflex.features.vehicle.domain.enums.VehicleBookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record VehicleBookingResponse(
        UUID id,
        UUID vehicleId,
        VehicleResponse vehicle,
        UUID tenantId,
        com.homeflex.core.dto.response.UserDto tenant,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalPrice,
        String currency,
        VehicleBookingStatus status,
        BigDecimal platformFee,
        String message,
        String rejectionReason,
        String stripePaymentIntentId,
        LocalDateTime createdAt
) {}
