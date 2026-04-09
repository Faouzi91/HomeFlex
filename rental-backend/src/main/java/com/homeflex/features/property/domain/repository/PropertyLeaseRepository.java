package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.PropertyLease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PropertyLeaseRepository extends JpaRepository<PropertyLease, UUID> {
    List<PropertyLease> findByPropertyId(UUID propertyId);
    List<PropertyLease> findByBookingId(UUID bookingId);
    List<PropertyLease> findByLandlordId(UUID landlordId);
    List<PropertyLease> findByTenantId(UUID tenantId);
    Optional<PropertyLease> findByBookingIdAndStatus(UUID bookingId, String status);
}
