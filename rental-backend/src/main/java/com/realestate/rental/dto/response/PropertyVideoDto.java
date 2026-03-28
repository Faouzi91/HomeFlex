package com.realestate.rental.dto.response;

import java.util.UUID;

public record PropertyVideoDto(
        UUID id,
        String videoUrl,
        String thumbnailUrl,
        Integer durationSeconds
) {}
