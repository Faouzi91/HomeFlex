package com.realestate.rental.shared.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiError {
    private final String code;
    private final String message;
    private final LocalDateTime timestamp;
}
