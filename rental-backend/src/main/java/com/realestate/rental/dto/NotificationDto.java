package com.realestate.rental.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

// NotificationDto.java
@Data
public class NotificationDto {
    private UUID id;
    private String title;
    private String message;
    private String type;
    private String relatedEntityType;
    private UUID relatedEntityId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
