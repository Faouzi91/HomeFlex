package com.realestate.rental.dto;

public record AuthResponse(
        String token,
        String refreshToken,
        UserDto user
) {}

