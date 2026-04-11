package com.homeflex.features.property.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ReviewCreateRequest(
        UUID propertyId,
        UUID targetUserId,
        @NotNull(message = "Rating is required") 
        @Min(value = 1, message = "Rating must be at least 1") 
        @Max(value = 5, message = "Rating must not exceed 5") 
        Integer rating,
        Integer cleanlinessRating,
        Integer accuracyRating,
        Integer communicationRating,
        Integer locationRating,
        Integer checkinRating,
        Integer valueRating,
        @Size(max = 1000, message = "Comment must not exceed 1000 characters") 
        String comment
) {}
