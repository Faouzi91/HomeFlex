package com.realestate.rental.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReportListingRequest(
        @NotBlank(message = "Reason is required") String reason,
        String description
) {}
