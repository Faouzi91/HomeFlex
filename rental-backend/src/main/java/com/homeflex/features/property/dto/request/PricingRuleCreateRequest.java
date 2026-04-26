package com.homeflex.features.property.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PricingRuleCreateRequest(
        @NotBlank String ruleType,
        String label,
        @NotNull @DecimalMin("0.01") BigDecimal multiplier,
        Integer minStayDays,
        LocalDate startDate,
        LocalDate endDate
) {}
