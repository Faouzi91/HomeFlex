package com.homeflex.features.property.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record RoomTypeCreateRequest(
        @NotBlank String name,
        String description,
        String bedType,
        @Min(1) Integer numBeds,
        @Min(1) Integer maxOccupancy,
        @NotNull BigDecimal pricePerNight,
        String currency,
        @Min(1) Integer totalRooms,
        BigDecimal sizeSqm,
        List<UUID> amenityIds
) {
    public RoomTypeCreateRequest {
        if (bedType == null || bedType.isBlank()) bedType = "DOUBLE";
        if (numBeds == null) numBeds = 1;
        if (maxOccupancy == null) maxOccupancy = 2;
        if (currency == null || currency.isBlank()) currency = "XAF";
        if (totalRooms == null) totalRooms = 1;
    }
}
