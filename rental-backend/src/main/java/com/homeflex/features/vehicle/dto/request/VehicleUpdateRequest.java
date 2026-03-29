package com.homeflex.features.vehicle.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record VehicleUpdateRequest(
        String description,

        @DecimalMin(value = "0.01", message = "Daily price must be greater than 0")
        BigDecimal dailyPrice,

        @Size(max = 100)
        String pickupCity,

        String pickupAddress,

        @Min(0)
        Integer mileage,

        @Size(max = 50)
        String color
) {}
