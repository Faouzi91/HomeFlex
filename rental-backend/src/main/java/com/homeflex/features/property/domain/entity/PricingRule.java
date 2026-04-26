package com.homeflex.features.property.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pricing_rules")
@Data
@NoArgsConstructor
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "rule_type", nullable = false, length = 20)
    private String ruleType; // WEEKEND | SEASONAL | LONG_STAY

    @Column(length = 100)
    private String label;

    /** 1.25 = +25%, 0.85 = -15% off base price */
    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal multiplier;

    /** LONG_STAY: rule activates when total stay >= this */
    @Column(name = "min_stay_days")
    private Integer minStayDays;

    /** SEASONAL: inclusive start date */
    @Column(name = "start_date")
    private LocalDate startDate;

    /** SEASONAL: inclusive end date */
    @Column(name = "end_date")
    private LocalDate endDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
