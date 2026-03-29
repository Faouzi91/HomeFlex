package com.homeflex.features.property.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RejectReasonRequest(
        @NotBlank(message = "Reason is required") String reason
) {}
