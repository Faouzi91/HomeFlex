package com.realestate.rental.utils.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.realestate.rental.utils.enumeration.BookingStatus;
import com.realestate.rental.utils.enumeration.BookingType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    @Column(name = "entity_version")
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", nullable = false, length = 20)
    private BookingType bookingType;

    @Column(name = "requested_date")
    private LocalDateTime requestedDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "number_of_occupants")
    private Integer numberOfOccupants;

    @Column(name = "landlord_response", columnDefinition = "TEXT")
    private String landlordResponse;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

