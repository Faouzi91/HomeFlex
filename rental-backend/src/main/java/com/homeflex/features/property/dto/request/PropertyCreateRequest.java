package com.homeflex.features.property.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PropertyCreateRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String propertyType,
        @NotBlank String listingType,
        @NotNull BigDecimal price,
        String currency,
        @NotBlank String address,
        @NotBlank String city,
        String stateProvince,
        @NotBlank String country,
        String postalCode,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer bedrooms,
        Integer bathrooms,
        BigDecimal areaSqm,
        Integer floorNumber,
        Integer totalFloors,
        String cancellationPolicy,
        BigDecimal cleaningFee,
        BigDecimal securityDeposit,
        LocalDate availableFrom,
        List<UUID> amenityIds
) {
    public PropertyCreateRequest {
        if (currency == null || currency.isBlank()) currency = "XAF";
        if (cancellationPolicy == null || cancellationPolicy.isBlank()) cancellationPolicy = "FLEXIBLE";
        if (cleaningFee == null) cleaningFee = BigDecimal.ZERO;
        if (securityDeposit == null) securityDeposit = BigDecimal.ZERO;
    }
}
