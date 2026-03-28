package com.realestate.rental.dto.response;

import java.util.UUID;

public record PropertyImageDto(
        UUID id,
        String imageUrl,
        String thumbnailUrl,
        Integer displayOrder,
        Boolean isPrimary
) {}
