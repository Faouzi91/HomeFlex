package com.homeflex.features.property.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AdminPricingRuleDto(
        UUID id,
        UUID propertyId,
        String propertyTitle,
        String ruleType,
        String label,
        BigDecimal multiplier,
        Integer minStayDays,
        LocalDate startDate,
        LocalDate endDate,
        LocalDateTime createdAt
) {}
