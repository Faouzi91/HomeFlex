package com.realestate.rental.dto;

import java.util.UUID;

public record TopPropertyDto(
        UUID id,
        String title,
        String city,
        Integer viewCount,
        Integer favoriteCount
) {}
