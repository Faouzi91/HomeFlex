package com.realestate.rental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FCMTokenRequest(
        @NotBlank(message = "FCM token is required") String token,
        @NotBlank(message = "Device type is required") @Pattern(regexp = "^(ANDROID|IOS|WEB)$", message = "Device type must be ANDROID, IOS, or WEB") String deviceType
) {}
