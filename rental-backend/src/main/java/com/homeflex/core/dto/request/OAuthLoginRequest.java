package com.homeflex.core.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OAuthLoginRequest(
        @NotBlank(message = "Token is required")
        String token
) {}
