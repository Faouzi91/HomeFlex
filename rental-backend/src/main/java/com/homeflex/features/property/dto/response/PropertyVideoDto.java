package com.homeflex.features.property.dto.response;

import java.util.UUID;

public record PropertyVideoDto(
        UUID id,
        String videoUrl,
        String thumbnailUrl,
        Integer durationSeconds
) {}
