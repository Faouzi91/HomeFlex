package com.realestate.rental.domain.repository;

import com.realestate.rental.domain.entity.Amenity;
import com.realestate.rental.domain.enums.AmenityCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// AmenityRepository.java
@Repository
public interface AmenityRepository extends JpaRepository<Amenity, UUID> {
    List<Amenity> findByCategory(AmenityCategory category);
}
