package com.realestate.rental.dto;

import java.util.UUID;

public record AmenityDto(
        UUID id,
        String name,
        String nameFr,
        String icon,
        String category
) {}
