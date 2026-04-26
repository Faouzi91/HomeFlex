package com.homeflex.core.exception;

/**
 * Thrown when a booking (or other stateful entity) is asked to transition
 * to a status that is not reachable from its current status.
 *
 * Mapped to HTTP 409 Conflict by GlobalExceptionHandler.
 */
public class IllegalStateTransitionException extends DomainException {

    private final String fromStatus;
    private final String toStatus;

    public IllegalStateTransitionException(String fromStatus, String toStatus) {
        super("Invalid state transition: " + fromStatus + " → " + toStatus);
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }
}
