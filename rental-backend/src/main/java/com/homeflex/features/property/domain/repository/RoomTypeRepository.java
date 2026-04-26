package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomTypeRepository extends JpaRepository<RoomType, UUID> {
    List<RoomType> findByPropertyIdOrderByCreatedAtAsc(UUID propertyId);
    List<RoomType> findByPropertyIdAndIsActiveTrueOrderByCreatedAtAsc(UUID propertyId);
    boolean existsByPropertyId(UUID propertyId);
    long countByPropertyIdAndIsActiveTrue(UUID propertyId);
}
