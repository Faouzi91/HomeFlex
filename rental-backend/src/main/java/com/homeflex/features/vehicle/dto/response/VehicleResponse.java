package com.homeflex.features.vehicle.dto.response;

import com.homeflex.core.dto.response.UserDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
        List<VehicleImageDto> images,
        UserDto owner,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
