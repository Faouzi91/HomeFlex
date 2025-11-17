package com.realestate.rental.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

// ChatRoomDto.java
@Data
public class ChatRoomDto {
    private UUID id;
    private UUID propertyId;
    private String propertyTitle;
    private UserDto tenant;
    private UserDto landlord;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;
}
