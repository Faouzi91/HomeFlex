package com.homeflex.features.property.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PropertyUpdateRequest(
        String title,
        String description,
        BigDecimal price,
        Boolean isAvailable,
        LocalDate availableFrom
) {}
