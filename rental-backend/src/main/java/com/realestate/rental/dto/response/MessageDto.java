package com.realestate.rental.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageDto(
        UUID id,
        UUID chatRoomId,
        UUID senderId,
        String senderName,
        String messageText,
        Boolean isRead,
        LocalDateTime createdAt
) {}
