package com.homeflex.features.vehicle.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record VehicleResponse(
        UUID id,
        UUID ownerId,
        String brand,
        String model,
        Integer year,
        String transmission,
        String fuelType,
        BigDecimal dailyPrice,
        String currency,
        String status,
        String description,
        Integer mileage,
        Integer seats,
        String color,
        String licensePlate,
        String pickupCity,
        String pickupAddress,
        int viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
