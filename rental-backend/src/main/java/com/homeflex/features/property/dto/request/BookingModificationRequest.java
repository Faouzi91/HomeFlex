package com.homeflex.features.property.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BookingModificationRequest(
        @NotNull(message = "Start date is required") LocalDate startDate,
        @NotNull(message = "End date is required") LocalDate endDate,
        String reason
) {}
