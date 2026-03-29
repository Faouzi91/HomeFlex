package com.homeflex.features.property.dto.response;

import com.homeflex.core.dto.response.UserDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReportDto(
        UUID id,
        UUID propertyId,
        String propertyTitle,
        UserDto reporter,
        String reason,
        String description,
        String status,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt,
        UserDto resolvedBy
) {}