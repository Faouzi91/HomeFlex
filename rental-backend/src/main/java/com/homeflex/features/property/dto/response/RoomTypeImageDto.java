package com.homeflex.features.property.dto.response;

import java.util.UUID;

public record RoomTypeImageDto(
        UUID id,
        String imageUrl,
        Integer displayOrder,
        Boolean isPrimary
) {}
