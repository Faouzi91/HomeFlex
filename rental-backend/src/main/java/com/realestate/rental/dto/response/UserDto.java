package com.realestate.rental.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String profilePictureUrl,
        String role,
        Boolean isActive,
        Boolean isVerified,
        String languagePreference,
        LocalDateTime createdAt
) {}
