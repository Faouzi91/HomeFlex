package com.realestate.rental.domain.repository;

import com.realestate.rental.domain.entity.PropertyVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// PropertyVideoRepository.java
@Repository
public interface PropertyVideoRepository extends JpaRepository<PropertyVideo, UUID> {
    List<PropertyVideo> findByPropertyId(UUID propertyId);
}
