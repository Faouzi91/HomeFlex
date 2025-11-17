package com.realestate.rental.utils.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class TypingNotification {
    private UUID chatRoomId;
    private UUID userId;
    private String userName;
    private boolean isTyping;
}
