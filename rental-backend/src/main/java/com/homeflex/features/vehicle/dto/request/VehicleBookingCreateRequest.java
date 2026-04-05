package com.homeflex.features.vehicle.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record VehicleBookingCreateRequest(
        @NotNull UUID vehicleId,
        @NotNull @FutureOrPresent LocalDate startDate,
        @NotNull LocalDate endDate,
        String message
) {}
