package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// ReviewRepository.java
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByPropertyIdOrderByCreatedAtDesc(UUID propertyId);

    Optional<Review> findByPropertyIdAndReviewerId(UUID propertyId, UUID reviewerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property.id = :propertyId")
    Double getAverageRatingByPropertyId(@Param("propertyId") UUID propertyId);
}
