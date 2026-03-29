package com.homeflex.core.dto.response;

public record AuthResponse(
        String token,
        String refreshToken,
        UserDto user
) {}

