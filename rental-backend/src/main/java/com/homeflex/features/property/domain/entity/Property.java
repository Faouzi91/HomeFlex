package com.homeflex.features.property.domain.entity;

import com.homeflex.core.domain.entity.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.homeflex.features.property.domain.enums.ListingType;
import com.homeflex.features.property.domain.enums.PropertyStatus;
import com.homeflex.features.property.domain.enums.PropertyType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Audited(withModifiedFlag = true)
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    @Column(name = "entity_version")
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "properties"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User landlord;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false, length = 50)
    private PropertyType propertyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type", nullable = false, length = 20)
    private ListingType listingType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(length = 3)
    private String currency = "XAF";

    // Location
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "state_province", length = 100)
    private String stateProvince;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    // Property Details
    private Integer bedrooms;

    private Integer bathrooms;

    @Column(name = "area_sqm", precision = 10, scale = 2)
    private BigDecimal areaSqm;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "total_floors")
    private Integer totalFloors;

    @Column(name = "cancellation_policy", length = 20)
    private String cancellationPolicy = "FLEXIBLE";

    @Column(name = "cleaning_fee", precision = 12, scale = 2)
    private BigDecimal cleaningFee = BigDecimal.ZERO;

    @Column(name = "security_deposit", precision = 12, scale = 2)
    private BigDecimal securityDeposit = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private com.homeflex.core.domain.entity.Agency agency;

    // Availability
    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "available_from")
    private LocalDate availableFrom;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PropertyStatus status = PropertyStatus.PENDING;

    // Metadata
    @Column(name = "view_count")
    @NotAudited
    private Integer viewCount = 0;

    @Column(name = "favorite_count")
    @NotAudited
    private Integer favoriteCount = 0;

    // Relationships
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"property"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @NotAudited
    private Set<PropertyImage> images = new HashSet<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"property"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @NotAudited
    private Set<PropertyVideo> videos = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "property_amenities",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    @JsonIgnoreProperties({"properties"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @NotAudited
    private Set<Amenity> amenities = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
