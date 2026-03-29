package com.homeflex.features.property.dto.response;

import java.util.UUID;

public record AmenityDto(
        UUID id,
        String name,
        String nameFr,
        String icon,
        String category
) {}
