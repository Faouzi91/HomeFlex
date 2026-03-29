package com.homeflex.features.vehicle.dto.response;

import java.util.UUID;

public record VehicleImageDto(
        UUID id,
        String imageUrl,
        int displayOrder,
        boolean isPrimary
) {}
