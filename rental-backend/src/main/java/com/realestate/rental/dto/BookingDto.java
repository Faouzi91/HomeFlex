package com.realestate.rental.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// BookingDto.java
@Data
public class BookingDto {
    private UUID id;
    private PropertyDto property;
    private UserDto tenant;
    private String bookingType;
    private LocalDateTime requestedDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String message;
    private Integer numberOfOccupants;
    private String landlordResponse;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;
}
