package com.homeflex.features.property.domain.entity;

import com.homeflex.features.property.domain.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable audit record for every booking state transition.
 *
 * One row is written per status change, capturing who did it, when,
 * and an optional human-readable reason (e.g. rejection reason).
 */
@Entity
@Table(name = "booking_audit_log", indexes = {
        @Index(name = "idx_booking_audit_log_booking", columnList = "booking_id"),
        @Index(name = "idx_booking_audit_log_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    private BookingStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    private BookingStatus toStatus;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "user_id")
    private UUID userId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ── Factory method ───────────────────────────────────────────────────────

    public static BookingAuditLog of(UUID bookingId, BookingStatus from, BookingStatus to,
                                     String action, UUID userId, String reason) {
        var log = new BookingAuditLog();
        log.setBookingId(bookingId);
        log.setFromStatus(from);
        log.setToStatus(to);
        log.setAction(action);
        log.setUserId(userId);
        log.setReason(reason);
        return log;
    }
}
