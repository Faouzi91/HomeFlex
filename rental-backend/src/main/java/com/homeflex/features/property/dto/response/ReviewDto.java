package com.homeflex.features.property.dto.response;

import com.homeflex.core.dto.response.UserDto;
import com.homeflex.features.property.domain.enums.ReviewType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewDto(
        UUID id,
        ReviewType type,
        UUID propertyId,
        UserDto targetUser,
        UserDto reviewer,
        Integer rating,
        Integer cleanlinessRating,
        Integer accuracyRating,
        Integer communicationRating,
        Integer locationRating,
        Integer checkinRating,
        Integer valueRating,
        String comment,
        String landlordReply,
        LocalDateTime repliedAt,
        LocalDateTime createdAt
) {}
