package com.realestate.rental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageSendRequest {
    @NotBlank(message = "Message cannot be empty")
    @Size(max = 5000, message = "Message too long")
    private String message;
}
