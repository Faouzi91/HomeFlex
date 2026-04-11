package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, UUID> {

    List<MaintenanceRequest> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

    @Query("SELECT mr FROM MaintenanceRequest mr WHERE mr.property.landlord.id = :landlordId ORDER BY mr.createdAt DESC")
    List<MaintenanceRequest> findByLandlordIdOrderByCreatedAtDesc(@Param("landlordId") UUID landlordId);

    List<MaintenanceRequest> findByPropertyIdOrderByCreatedAtDesc(UUID propertyId);
}
