package com.realestate.rental.repository;

import com.realestate.rental.utils.entity.Booking;
import com.realestate.rental.utils.enumeration.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
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
}
