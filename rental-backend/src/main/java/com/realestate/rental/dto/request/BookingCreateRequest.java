package com.realestate.rental.dto.request;

import com.google.firebase.database.annotations.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// BookingCreateRequest.java
@Data
public class BookingCreateRequest {
    @NotNull
    private UUID propertyId;

    @NotBlank
    private String bookingType;

    private LocalDateTime requestedDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String message;
    private Integer numberOfOccupants;
}
