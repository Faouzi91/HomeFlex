package com.realestate.rental.dto.response;

import java.util.UUID;

public record TopPropertyDto(
        UUID id,
        String title,
        String city,
        Integer viewCount,
        Integer favoriteCount
) {}
