package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// FavoriteRepository.java
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    List<Favorite> findByUserId(UUID userId);

    Optional<Favorite> findByUserIdAndPropertyId(UUID userId, UUID propertyId);

    boolean existsByUserIdAndPropertyId(UUID userId, UUID propertyId);

    void deleteByUserIdAndPropertyId(UUID userId, UUID propertyId);
}
