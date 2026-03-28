package com.realestate.rental.domain.repository;

import com.realestate.rental.domain.entity.Booking;
import com.realestate.rental.domain.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
}
