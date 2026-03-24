package com.realestate.rental.shared.exception;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
