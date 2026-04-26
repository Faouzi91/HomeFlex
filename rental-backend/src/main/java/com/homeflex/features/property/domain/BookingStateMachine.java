package com.homeflex.features.property.domain;

import com.homeflex.core.exception.IllegalStateTransitionException;
import com.homeflex.features.property.domain.enums.BookingStatus;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Pure-domain state machine for the booking lifecycle.
 *
 * Centralises every valid status transition in one immutable lookup table.
 * The service layer calls {@link #transition} before mutating status —
 * an {@link IllegalStateTransitionException} is thrown if the move is illegal.
 *
 * <p>This class is deliberately free of Spring annotations so it can be
 * unit-tested without a container.
 */
public final class BookingStateMachine {

    private BookingStateMachine() {}

    /**
     * Allowed transitions: key = current status, value = set of reachable statuses.
     */
    private static final Map<BookingStatus, Set<BookingStatus>> TRANSITIONS;

    static {
        var map = new EnumMap<BookingStatus, Set<BookingStatus>>(BookingStatus.class);

        map.put(BookingStatus.DRAFT, EnumSet.of(
                BookingStatus.PAYMENT_PENDING,
                BookingStatus.PENDING_APPROVAL,   // VIEWING bookings skip payment
                BookingStatus.APPROVED,           // Instant Book: skip approval entirely
                BookingStatus.CANCELLED
        ));

        map.put(BookingStatus.PAYMENT_PENDING, EnumSet.of(
                BookingStatus.PENDING_APPROVAL,   // payment succeeded, awaiting landlord
                BookingStatus.APPROVED,           // Instant Book: auto-approve after payment
                BookingStatus.PAYMENT_FAILED,     // payment failed
                BookingStatus.CANCELLED
        ));

        map.put(BookingStatus.PAYMENT_FAILED, EnumSet.of(
                BookingStatus.PAYMENT_PENDING,    // retry
                BookingStatus.CANCELLED
        ));

        map.put(BookingStatus.PENDING_APPROVAL, EnumSet.of(
                BookingStatus.APPROVED,
                BookingStatus.REJECTED,
                BookingStatus.CANCELLED
        ));

        map.put(BookingStatus.APPROVED, EnumSet.of(
                BookingStatus.ACTIVE,
                BookingStatus.CANCELLED,
                BookingStatus.PENDING_MODIFICATION
        ));

        map.put(BookingStatus.PENDING_MODIFICATION, EnumSet.of(
                BookingStatus.APPROVED            // approve or reject modification both return to APPROVED
        ));

        map.put(BookingStatus.ACTIVE, EnumSet.of(
                BookingStatus.COMPLETED
        ));

        // Terminal states — no outgoing transitions
        map.put(BookingStatus.REJECTED, EnumSet.noneOf(BookingStatus.class));
        map.put(BookingStatus.CANCELLED, EnumSet.noneOf(BookingStatus.class));
        map.put(BookingStatus.COMPLETED, EnumSet.noneOf(BookingStatus.class));

        TRANSITIONS = Map.copyOf(map);
    }

    /**
     * Checks whether a transition from {@code from} to {@code to} is legal.
     */
    public static boolean canTransition(BookingStatus from, BookingStatus to) {
        Set<BookingStatus> allowed = TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    /**
     * Validates and returns the target status, or throws if illegal.
     *
     * @param currentStatus the booking's current status
     * @param targetStatus  the desired new status
     * @return {@code targetStatus} if the transition is valid
     * @throws IllegalStateTransitionException if the transition is invalid
     */
    public static BookingStatus transition(BookingStatus currentStatus, BookingStatus targetStatus) {
        if (!canTransition(currentStatus, targetStatus)) {
            throw new IllegalStateTransitionException(currentStatus.name(), targetStatus.name());
        }
        return targetStatus;
    }
}
