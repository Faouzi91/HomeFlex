package com.homeflex.features.property.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PricingRuleDto(
        UUID id,
        UUID propertyId,
        String ruleType,
        String label,
        BigDecimal multiplier,
        Integer minStayDays,
        LocalDate startDate,
        LocalDate endDate
) {}
