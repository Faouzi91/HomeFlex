package com.homeflex.features.vehicle.domain.entity;

import com.homeflex.features.vehicle.domain.enums.VehicleBookingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vehicle_bookings", schema = "vehicles")
@Data
@NoArgsConstructor
public class VehicleBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false, length = 3)
    private String currency = "XAF";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleBookingStatus status = VehicleBookingStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Version
    @Column(name = "entity_version", nullable = false)
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
