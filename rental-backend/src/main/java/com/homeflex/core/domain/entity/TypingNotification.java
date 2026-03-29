package com.homeflex.core.domain.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class TypingNotification {
    private UUID chatRoomId;
    private UUID userId;
    private String userName;
    private boolean isTyping;
}
