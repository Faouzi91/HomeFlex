package com.homeflex.features.property.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PropertyUpdateRequest(
        String title,
        String description,
        BigDecimal price,
        Integer bedrooms,
        Integer bathrooms,
        List<UUID> amenityIds,
        Boolean isAvailable,
        LocalDate availableFrom
) {}
