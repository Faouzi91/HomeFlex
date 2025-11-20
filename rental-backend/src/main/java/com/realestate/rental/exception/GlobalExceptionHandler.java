package com.realestate.rental.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // -------------------------------
    // Unified Error Response Object
    // -------------------------------
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResponse {
        @Builder.Default
        private LocalDateTime timestamp = LocalDateTime.now();
        private String message;
        private int status;
        private String path;
        private Map<String, String> errors;
    }

    // -------------------------------------------------------
    // 401 - BAD CREDENTIALS
    // -------------------------------------------------------
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex,
            WebRequest request
    ) {
        return buildError(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password",
                request,
                null
        );
    }

    // -------------------------------------------------------
    // 403 - ACCESS DENIED
    // -------------------------------------------------------
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request
    ) {
        return buildError(
                HttpStatus.FORBIDDEN,
                "Access denied",
                request,
                null
        );
    }

    // -------------------------------------------------------
    // 400 - VALIDATION ERRORS (Single Unified Handler)
    // -------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return buildError(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                errors
        );
    }

    // -------------------------------------------------------
    // 400 - ILLEGAL ARGUMENT
    // -------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request
    ) {
        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                null
        );
    }

    // -------------------------------------------------------
    // 400 - RUNTIME EXCEPTIONS
    // -------------------------------------------------------
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            WebRequest request
    ) {
        log.error("Runtime exception occurred", ex);
        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage() != null ? ex.getMessage() : "A runtime error occurred",
                request,
                null
        );
    }

    // -------------------------------------------------------
    // 500 - GENERIC CATCH-ALL
    // -------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request
    ) {
        log.error("Unhandled exception occurred", ex);
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage() != null ? ex.getMessage() : "An internal error occurred",
                request,
                null
        );
    }

    // -------------------------------------------------------
    // UTILITY - BUILD ERROR RESPONSE
    // -------------------------------------------------------
    private ResponseEntity<ErrorResponse> buildError(
            HttpStatus status,
            String message,
            WebRequest request,
            Map<String, String> fieldErrors
    ) {
        String path = request.getDescription(false).replace("uri=", "");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .path(path)
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}
