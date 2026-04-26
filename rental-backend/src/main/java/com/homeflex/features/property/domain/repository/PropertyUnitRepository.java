package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.PropertyUnit;
import com.homeflex.features.property.domain.enums.UnitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyUnitRepository extends JpaRepository<PropertyUnit, UUID> {

    List<PropertyUnit> findByRoomTypeIdOrderByUnitNumberAsc(UUID roomTypeId);

    long countByRoomTypeId(UUID roomTypeId);

    /**
     * Returns all units of a room type whose status is AVAILABLE *and* that have
     * no overlapping non-terminal booking in [start, end). Terminal statuses
     * (CANCELLED, REJECTED, COMPLETED) free the unit.
     *
     * Overlap rule: existing.start < end AND existing.end > start.
     */
    @Query("""
        SELECT u FROM PropertyUnit u
        WHERE u.roomType.id = :roomTypeId
          AND u.status = com.homeflex.features.property.domain.enums.UnitStatus.AVAILABLE
          AND NOT EXISTS (
              SELECT 1 FROM Booking b
              WHERE b.unit = u
                AND b.startDate < :endDate
                AND b.endDate   > :startDate
                AND b.status NOT IN (
                    com.homeflex.features.property.domain.enums.BookingStatus.CANCELLED,
                    com.homeflex.features.property.domain.enums.BookingStatus.REJECTED,
                    com.homeflex.features.property.domain.enums.BookingStatus.COMPLETED
                )
          )
        ORDER BY u.unitNumber ASC
        """)
    List<PropertyUnit> findAvailableInRange(
            @Param("roomTypeId") UUID roomTypeId,
            @Param("startDate")  LocalDate startDate,
            @Param("endDate")    LocalDate endDate);

    List<PropertyUnit> findByRoomTypeIdAndStatus(UUID roomTypeId, UnitStatus status);
}
