package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.UUID;

// BookingRepository.java
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

    List<Booking> findByPropertyIdOrderByCreatedAtDesc(UUID propertyId);

    @Query("SELECT b FROM Booking b WHERE b.property.landlord.id = :landlordId " +
            "ORDER BY b.createdAt DESC")
    List<Booking> findByLandlordId(@Param("landlordId") UUID landlordId);

    Optional<Booking> findByStripePaymentIntentId(String stripePaymentIntentId);

    boolean existsByPropertyIdAndTenantIdAndStatusIn(UUID propertyId, UUID tenantId, List<BookingStatus> statuses);

    boolean existsByTenantIdAndPropertyLandlordIdAndStatusIn(UUID tenantId, UUID landlordId, List<BookingStatus> statuses);

    List<Booking> findByStatus(BookingStatus status);

    // Analytics methods
    long countByStatus(BookingStatus status);

    @Query("SELECT b.status, COUNT(b) FROM Booking b GROUP BY b.status")
    List<Object[]> countByStatusGrouped();

    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            WHERE b.property.id = :propertyId
              AND b.status IN :statuses
              AND b.startDate IS NOT NULL
              AND b.endDate IS NOT NULL
              AND b.startDate <= :endDate
              AND b.endDate >= :startDate
            """)
    boolean existsDateOverlapForProperty(
            @Param("propertyId") UUID propertyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<BookingStatus> statuses
    );

    /**
     * Finds approved bookings that have reached check-in date and whose
     * escrow has not yet been released.
     */
    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.property p
            JOIN FETCH p.landlord
            WHERE b.status = :status
              AND b.stripePaymentIntentId IS NOT NULL
              AND b.escrowReleasedAt IS NULL
              AND b.startDate <= :today
            """)
    List<Booking> findEscrowReady(
            @Param("status") BookingStatus status,
            @Param("today") LocalDate today
    );

    /**
     * Sum of total_price minus platform_fee for bookings still in escrow
     * (approved, paid, not yet released) for a given landlord.
     */
    @Query("""
            SELECT SUM(b.totalPrice - COALESCE(b.platformFee, 0))
            FROM Booking b
            WHERE b.property.landlord.id = :landlordId
              AND b.status = :status
              AND b.stripePaymentIntentId IS NOT NULL
              AND b.escrowReleasedAt IS NULL
            """)
    Optional<BigDecimal> sumEscrowHeldByLandlord(
            @Param("landlordId") UUID landlordId,
            @Param("status") BookingStatus status
    );

    /**
     * Sum of total_price minus platform_fee for bookings whose escrow
     * has been released (total earnings) for a given landlord.
     */
    @Query("""
            SELECT SUM(b.totalPrice - COALESCE(b.platformFee, 0))
            FROM Booking b
            WHERE b.property.landlord.id = :landlordId
              AND b.escrowReleasedAt IS NOT NULL
            """)
    Optional<BigDecimal> sumReleasedByLandlord(@Param("landlordId") UUID landlordId);
}
