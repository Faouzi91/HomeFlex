package com.homeflex.features.property.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReportListingRequest(
        @NotBlank(message = "Reason is required") String reason,
        String description
) {}
