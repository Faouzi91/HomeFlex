package com.realestate.rental.repository;

import com.realestate.rental.utils.entity.ReportedListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// ====================================
// ReportedListingRepository - Additional Methods
// ====================================
@Repository
public interface ReportedListingRepository extends JpaRepository<ReportedListing, UUID> {
    List<ReportedListing> findByPropertyId(UUID propertyId);
    Page<ReportedListing> findByStatus(String status, Pageable pageable);
    List<ReportedListing> findByStatus(String status);
}
