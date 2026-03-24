package com.realestate.rental.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportListingRequest {
    @NotBlank(message = "Reason is required")
    private String reason;

    private String description;
}
