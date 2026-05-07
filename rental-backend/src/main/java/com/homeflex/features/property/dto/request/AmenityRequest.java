package com.homeflex.features.property.dto.request;

import com.homeflex.features.property.domain.enums.AmenityCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AmenityRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 100) String nameFr,
        @Size(max = 50) String icon,
        @NotNull AmenityCategory category
) {}
