package com.homeflex.features.insurance.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record InsurancePlanResponse(
        UUID id,
        String providerName,
        String name,
        String type,
        String description,
        String coverageDetails,
        BigDecimal dailyPremium,
        BigDecimal maxCoverageAmount
) {}
