package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.Review;
import com.homeflex.features.property.domain.enums.ReviewType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByPropertyIdAndTypeOrderByCreatedAtDesc(UUID propertyId, ReviewType type);

    List<Review> findByTargetUserIdAndTypeOrderByCreatedAtDesc(UUID targetUserId, ReviewType type);

    Optional<Review> findByPropertyIdAndReviewerIdAndType(UUID propertyId, UUID reviewerId, ReviewType type);

    Optional<Review> findByTargetUserIdAndReviewerIdAndType(UUID targetUserId, UUID reviewerId, ReviewType type);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property.id = :propertyId AND r.type = 'PROPERTY'")
    Double getAverageRatingByPropertyId(@Param("propertyId") UUID propertyId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.targetUser.id = :userId AND r.type = 'TENANT'")
    Double getAverageRatingByUserId(@Param("userId") UUID userId);
}
