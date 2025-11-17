package com.realestate.rental.repository;

import com.realestate.rental.utils.entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// PropertyImageRepository.java
@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, UUID> {
    List<PropertyImage> findByPropertyIdOrderByDisplayOrderAsc(UUID propertyId);
}
