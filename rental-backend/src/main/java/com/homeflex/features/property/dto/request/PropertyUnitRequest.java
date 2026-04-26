package com.homeflex.features.property.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PropertyUnitRequest(
        @NotBlank @Size(max = 50) String unitNumber,
        Integer floor,
        @Size(max = 20) String status,
        String notes
) {}
