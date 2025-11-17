package com.realestate.rental.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReviewDto {
    private UUID id;
    private UUID propertyId;
    private UserDto reviewer;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
