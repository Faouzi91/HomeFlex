package com.homeflex.features.property.dto.request;

import jakarta.validation.constraints.*;

public record CancellationPolicyRequest(
        @NotBlank @Size(max = 40) String code,
        @NotBlank @Size(max = 120) String name,
        String description,
        @NotNull @Min(0) @Max(100) Integer refundPercentage,
        @NotNull @Min(0) Integer hoursBeforeCheckin,
        Boolean isActive
) {}
