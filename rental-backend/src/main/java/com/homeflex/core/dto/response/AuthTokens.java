package com.homeflex.core.dto.response;

/**
 * Internal service result carrying JWT tokens + user data.
 * Never serialized to the API response body — tokens go into httpOnly cookies.
 */
public record AuthTokens(String accessToken, String refreshToken, UserDto user) {}
