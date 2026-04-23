package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    /**
     * Used by ResourcePermissionService for ownership checks.
     * Eagerly fetches tenant and property.landlord to avoid lazy-load round-trips
     * inside the PermissionEvaluator, which runs outside the service transaction.
     */
    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.tenant
            JOIN FETCH b.property p
            JOIN FETCH p.landlord
            WHERE b.id = :id
            """)
    Optional<Booking> findByIdWithParties(@Param("id") UUID id);

    List<Booking> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

    List<Booking> findByPropertyIdOrderByCreatedAtDesc(UUID propertyId);

    @Query("SELECT b FROM Booking b WHERE b.property.landlord.id = :landlordId " +
            "ORDER BY b.createdAt DESC")
    List<Booking> findByLandlordId(@Param("landlordId") UUID landlordId);

    Optional<Booking> findByStripePaymentIntentId(String stripePaymentIntentId);

    Optional<Booking> findByIdempotencyKey(String idempotencyKey);

    boolean existsByPropertyIdAndTenantIdAndStatusIn(UUID propertyId, UUID tenantId, List<BookingStatus> statuses);

    boolean existsByTenantIdAndPropertyLandlordIdAndStatusIn(UUID tenantId, UUID landlordId, List<BookingStatus> statuses);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByStatusAndCreatedAtBefore(BookingStatus status, LocalDateTime createdAt);

    List<Booking> findByStatusAndStartDateLessThanEqual(BookingStatus status, LocalDate date);

    List<Booking> findByStatusAndEndDateLessThan(BookingStatus status, LocalDate date);

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

    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            WHERE b.property.id = :propertyId
              AND b.id <> :excludeBookingId
              AND b.status IN :statuses
              AND b.startDate IS NOT NULL
              AND b.endDate IS NOT NULL
              AND b.startDate <= :endDate
              AND b.endDate >= :startDate
            """)
    boolean existsDateOverlapForPropertyExcludingBooking(
            @Param("propertyId") UUID propertyId,
            @Param("excludeBookingId") UUID excludeBookingId,
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
