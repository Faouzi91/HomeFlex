package com.homeflex.features.vehicle.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "condition_reports", schema = "vehicles")
@Data
@NoArgsConstructor
public class ConditionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Column(name = "booking_id")
    private UUID bookingId;

    @Column(name = "reporter_id", nullable = false)
    private UUID reporterId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String notes;

    @Column(name = "mileage_at")
    private Integer mileageAt;

    @Column(name = "fuel_level", length = 20)
    private String fuelLevel;

    @ElementCollection
    @CollectionTable(
            name = "condition_report_images",
            schema = "vehicles",
            joinColumns = @JoinColumn(name = "report_id")
    )
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
