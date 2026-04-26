package com.homeflex.features.property.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoomTypeDto(
        UUID id,
        UUID propertyId,
        String name,
        String description,
        String bedType,
        Integer numBeds,
        Integer maxOccupancy,
        BigDecimal pricePerNight,
        String currency,
        Integer totalRooms,
        BigDecimal sizeSqm,
        Boolean isActive,
        List<RoomTypeImageDto> images,
        List<AmenityDto> amenities,
        LocalDateTime createdAt
) {}
