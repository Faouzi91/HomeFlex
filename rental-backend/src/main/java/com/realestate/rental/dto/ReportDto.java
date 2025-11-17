package com.realestate.rental.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReportDto {
    private UUID id;
    private UUID propertyId;
    private String propertyTitle;
    private UserDto reporter;
    private String reason;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private UserDto resolvedBy;
}