package com.realestate.rental.dto.response;

public record AuthResponse(
        String token,
        String refreshToken,
        UserDto user
) {}

