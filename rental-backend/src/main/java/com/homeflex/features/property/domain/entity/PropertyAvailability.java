package com.homeflex.features.property.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/// One row per (property, date) that is *not* available. Absence of a row
/// means the date is bookable. Status distinguishes a host-imposed block
/// (BLOCKED) from an active booking reservation (BOOKED).
@Entity
@Table(name = "property_availability")
@Data
@NoArgsConstructor
public class PropertyAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    /// Set when status = BOOKED, so we can release the date when a booking
    /// is cancelled. Null for host-imposed blocks.
    @Column(name = "booking_id")
    private UUID bookingId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum Status { BLOCKED, BOOKED }
}
