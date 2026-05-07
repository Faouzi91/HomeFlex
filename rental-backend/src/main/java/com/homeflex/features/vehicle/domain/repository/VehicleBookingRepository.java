package com.homeflex.features.vehicle.domain.repository;

import com.homeflex.features.vehicle.domain.entity.VehicleBooking;
import com.homeflex.features.vehicle.domain.enums.VehicleBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleBookingRepository extends JpaRepository<VehicleBooking, UUID> {

    Optional<VehicleBooking> findByStripePaymentIntentId(String stripePaymentIntentId);

    List<VehicleBooking> findByVehicleId(UUID vehicleId);


    /**
     * Detects date overlaps for a vehicle using standard interval logic:
     * {@code existing.start <= requested.end AND existing.end >= requested.start}.
     * <p>
     * Only non-cancelled bookings are considered (PENDING + CONFIRMED).
     */
    @Query("""
            SELECT COUNT(b) > 0
            FROM VehicleBooking b
            WHERE b.vehicleId = :vehicleId
              AND b.status IN :statuses
              AND b.startDate <= :endDate
              AND b.endDate   >= :startDate
            """)
    boolean existsDateOverlap(
            @Param("vehicleId") UUID vehicleId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<VehicleBookingStatus> statuses
    );

    List<VehicleBooking> findByVehicleIdAndStatusInOrderByStartDateAsc(
            UUID vehicleId, List<VehicleBookingStatus> statuses);

    List<VehicleBooking> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

    /**
     * Finds confirmed vehicle bookings that have reached check-in date and whose
     * escrow has not yet been released.
     */
    @Query("""
            SELECT b FROM VehicleBooking b
            WHERE b.status = :status
              AND b.stripePaymentIntentId IS NOT NULL
              AND b.escrowReleasedAt IS NULL
              AND b.startDate <= :today
            """)
    List<VehicleBooking> findEscrowReady(
            @Param("status") VehicleBookingStatus status,
            @Param("today") LocalDate today
    );

    /**
     * Sum of total_price minus platform_fee for vehicle bookings still in escrow
     * for a given vehicle owner.
     */
    @Query("""
            SELECT SUM(b.totalPrice - COALESCE(b.platformFee, 0))
            FROM VehicleBooking b
            JOIN com.homeflex.features.vehicle.domain.entity.Vehicle v
              ON v.id = b.vehicleId
            WHERE v.ownerId = :ownerId
              AND b.status = :status
              AND b.stripePaymentIntentId IS NOT NULL
              AND b.escrowReleasedAt IS NULL
            """)
    Optional<BigDecimal> sumEscrowHeldByOwner(
            @Param("ownerId") UUID ownerId,
            @Param("status") VehicleBookingStatus status
    );

    /**
     * Sum of total_price minus platform_fee for vehicle bookings whose escrow
     * has been released (total earnings) for a given owner.
     */
    @Query("""
            SELECT SUM(b.totalPrice - COALESCE(b.platformFee, 0))
            FROM VehicleBooking b
            JOIN com.homeflex.features.vehicle.domain.entity.Vehicle v
              ON v.id = b.vehicleId
            WHERE v.ownerId = :ownerId
              AND b.escrowReleasedAt IS NOT NULL
            """)
    Optional<BigDecimal> sumReleasedByOwner(@Param("ownerId") UUID ownerId);
}
