package com.homeflex.features.vehicle.dto.response;

import com.homeflex.features.vehicle.domain.enums.FuelType;
import com.homeflex.features.vehicle.domain.enums.Transmission;
import com.homeflex.features.vehicle.domain.enums.VehicleStatus;

import java.math.BigDecimal;

public record VehicleSearchParams(
        String brand,
        String model,
        String city,
        Transmission transmission,
        FuelType fuelType,
        VehicleStatus status,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {}
