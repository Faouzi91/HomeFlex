package com.homeflex.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageSendRequest(
        @NotBlank(message = "Message cannot be empty") @Size(max = 5000, message = "Message too long") String message
) {}
