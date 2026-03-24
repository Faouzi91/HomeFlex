package com.realestate.rental.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        String title,
        String message,
        String type,
        String relatedEntityType,
        UUID relatedEntityId,
        Boolean isRead,
        LocalDateTime createdAt
) {}
