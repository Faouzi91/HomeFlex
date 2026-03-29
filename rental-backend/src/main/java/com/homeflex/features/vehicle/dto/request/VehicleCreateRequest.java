package com.homeflex.features.vehicle.dto.request;

import com.homeflex.features.vehicle.domain.enums.FuelType;
import com.homeflex.features.vehicle.domain.enums.Transmission;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record VehicleCreateRequest(

        @NotBlank(message = "Brand is required")
        @Size(max = 100)
        String brand,

        @NotBlank(message = "Model is required")
        @Size(max = 100)
        String model,

        @NotNull(message = "Year is required")
        @Min(value = 1900, message = "Year must be at least 1900")
        @Max(value = 2100, message = "Year must be at most 2100")
        Integer year,

        @NotNull(message = "Transmission is required")
        Transmission transmission,

        @NotNull(message = "Fuel type is required")
        FuelType fuelType,

        @NotNull(message = "Daily price is required")
        @DecimalMin(value = "0.01", message = "Daily price must be greater than 0")
        BigDecimal dailyPrice,

        @Size(max = 3)
        String currency,

        String description,

        @Min(0)
        Integer mileage,

        @Min(1)
        Integer seats,

        @Size(max = 50)
        String color,

        @Size(max = 20)
        String licensePlate,

        @Size(max = 100)
        String pickupCity,

        String pickupAddress
) {
    public VehicleCreateRequest {
        if (currency == null || currency.isBlank()) {
            currency = "XAF";
        }
    }
}
