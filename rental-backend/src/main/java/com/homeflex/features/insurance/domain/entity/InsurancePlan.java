package com.homeflex.features.insurance.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "insurance_plans")
@Data
public class InsurancePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private InsuranceProvider provider;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type; // TENANT, LANDLORD, VEHICLE

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "coverage_details", columnDefinition = "jsonb")
    private String coverageDetails;

    @Column(name = "daily_premium", nullable = false)
    private BigDecimal dailyPremium;

    @Column(name = "max_coverage_amount")
    private BigDecimal maxCoverageAmount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
