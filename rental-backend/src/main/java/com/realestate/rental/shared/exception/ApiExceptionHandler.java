package com.realestate.rental.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(DomainException ex) {
        return ResponseEntity.badRequest().body(ApiError.builder()
                .code("DOMAIN_ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnhandledException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiError.builder()
                .code("INTERNAL_ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build());
    }
}
