package com.realestate.rental.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

// MessageDto.java
@Data
public class MessageDto {
    private UUID id;
    private UUID chatRoomId;
    private UUID senderId;
    private String senderName;
    private String messageText;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
