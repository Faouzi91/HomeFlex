package com.homeflex.features.property.dto.response;

import java.math.BigDecimal;

public record PricingRecommendationResponse(
        String propertyId,
        BigDecimal currentPrice,
        BigDecimal recommendedPrice,
        String confidenceLevel,
        String reasoning
) {}
