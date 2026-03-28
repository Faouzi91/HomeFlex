package com.realestate.rental.exception;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
