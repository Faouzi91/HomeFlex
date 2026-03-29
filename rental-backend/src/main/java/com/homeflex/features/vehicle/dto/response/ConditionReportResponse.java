package com.homeflex.features.vehicle.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ConditionReportResponse(
        UUID id,
        UUID vehicleId,
        UUID bookingId,
        UUID reporterId,
        String notes,
        Integer mileageAt,
        String fuelLevel,
        List<String> imageUrls,
        LocalDateTime createdAt
) {}
