package com.homeflex.core.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserDto(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String profilePictureUrl,
        String role,           // legacy — first matched enum value; kept for frontend compat
        List<String> roles,    // RBAC role names, e.g. ["ROLE_TENANT"]
        List<String> permissions, // flattened permission names, e.g. ["BOOKING_CREATE", ...]
        Boolean isActive,
        Boolean isVerified,
        String languagePreference,
        UUID agencyId,
        String agencyRole,
        Double trustScore,
        Boolean emailNotificationsEnabled,
        Boolean pushNotificationsEnabled,
        Boolean smsNotificationsEnabled,
        Integer profileCompleteness,
        LocalDateTime createdAt,
        Boolean stripeConnected,
        String stripeAccountId
) {}
