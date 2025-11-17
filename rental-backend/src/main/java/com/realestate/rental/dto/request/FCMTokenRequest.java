package com.realestate.rental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FCMTokenRequest {
    @NotBlank(message = "FCM token is required")
    private String token;

    @NotBlank(message = "Device type is required")
    @Pattern(regexp = "^(ANDROID|IOS|WEB)$", message = "Device type must be ANDROID, IOS, or WEB")
    private String deviceType;
}
