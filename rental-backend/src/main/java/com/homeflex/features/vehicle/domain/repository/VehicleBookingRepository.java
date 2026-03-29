package com.homeflex.features.vehicle.domain.repository;

import com.homeflex.features.vehicle.domain.entity.VehicleBooking;
import com.homeflex.features.vehicle.domain.enums.VehicleBookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface VehicleBookingRepository extends JpaRepository<VehicleBooking, UUID> {

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
}
