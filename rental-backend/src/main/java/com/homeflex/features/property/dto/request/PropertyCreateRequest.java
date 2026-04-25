package com.homeflex.features.property.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
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
        List<UUID> amenityIds,
        Boolean instantBookEnabled,
        // Policy fields
        LocalTime checkInTime,
        LocalTime checkOutTime,
        @Min(1) @Max(5) Integer starRating,
        Boolean petsAllowed,
        Boolean smokingAllowed,
        Boolean childrenAllowed,
        @Min(1) Integer minStayNights,
        Integer maxStayNights,
        String houseRules,
        /** When true, property starts as DRAFT instead of PENDING */
        Boolean submitAsDraft
) {
    public PropertyCreateRequest {
        if (currency == null || currency.isBlank()) currency = "XAF";
        if (cancellationPolicy == null || cancellationPolicy.isBlank()) cancellationPolicy = "FLEXIBLE";
        if (cleaningFee == null) cleaningFee = BigDecimal.ZERO;
        if (securityDeposit == null) securityDeposit = BigDecimal.ZERO;
        if (instantBookEnabled == null) instantBookEnabled = false;
        if (petsAllowed == null) petsAllowed = false;
        if (smokingAllowed == null) smokingAllowed = false;
        if (childrenAllowed == null) childrenAllowed = true;
        if (minStayNights == null) minStayNights = 1;
        if (submitAsDraft == null) submitAsDraft = false;
    }
}
