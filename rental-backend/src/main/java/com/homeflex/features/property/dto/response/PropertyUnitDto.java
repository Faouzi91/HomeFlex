package com.homeflex.features.property.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PropertyUnitDto(
        UUID id,
        UUID roomTypeId,
        String unitNumber,
        Integer floor,
        String status,
        String notes,
        LocalDateTime createdAt
) {}
