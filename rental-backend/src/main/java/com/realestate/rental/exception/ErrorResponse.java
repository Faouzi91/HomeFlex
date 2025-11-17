package com.realestate.rental.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

// ErrorResponse.java
@Data
@Builder
class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private int status;
}
