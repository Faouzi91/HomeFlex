package com.realestate.rental.dto;

import java.util.UUID;

public record PropertyImageDto(
        UUID id,
        String imageUrl,
        String thumbnailUrl,
        Integer displayOrder,
        Boolean isPrimary
) {}
