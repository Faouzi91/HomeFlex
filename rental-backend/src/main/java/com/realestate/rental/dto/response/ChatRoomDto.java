package com.realestate.rental.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatRoomDto(
        UUID id,
        UUID propertyId,
        String propertyTitle,
        UserDto tenant,
        UserDto landlord,
        LocalDateTime lastMessageAt,
        Integer unreadCount
) {}
