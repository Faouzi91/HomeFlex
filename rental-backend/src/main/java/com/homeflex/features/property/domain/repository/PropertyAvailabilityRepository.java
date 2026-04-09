package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.PropertyAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PropertyAvailabilityRepository
        extends JpaRepository<PropertyAvailability, UUID> {

    @Query("""
            SELECT a FROM PropertyAvailability a
            WHERE a.propertyId = :propertyId
              AND a.date BETWEEN :start AND :end
            ORDER BY a.date
            """)
    List<PropertyAvailability> findRange(
            @Param("propertyId") UUID propertyId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    /// Returns the count of unavailable dates in the range. If > 0, the
    /// range is not bookable. We use COUNT instead of fetching the rows so
    /// the query is index-only.
    @Query("""
            SELECT COUNT(a) FROM PropertyAvailability a
            WHERE a.propertyId = :propertyId
              AND a.date BETWEEN :start AND :endInclusive
            """)
    long countConflicts(
            @Param("propertyId") UUID propertyId,
            @Param("start") LocalDate start,
            @Param("endInclusive") LocalDate endInclusive);

    @Modifying
    @Query("""
            DELETE FROM PropertyAvailability a
            WHERE a.bookingId = :bookingId
            """)
    int deleteByBookingId(@Param("bookingId") UUID bookingId);
}
