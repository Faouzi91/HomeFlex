package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.enums.PropertyStatus;
import com.homeflex.features.property.domain.enums.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// PropertyRepository.java
@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID>,
        JpaSpecificationExecutor<Property> {

    // Override findAll to eagerly fetch data in ONE query
    @Override
    @EntityGraph(attributePaths = {"images", "amenities", "landlord", "videos"})
    Page<Property> findAll(Specification<Property> spec, Pageable pageable);

    List<Property> findByLandlordId(UUID landlordId);

    List<Property> findByStatus(PropertyStatus status);

    Page<Property> findByStatus(PropertyStatus status, Pageable pageable);


    @Query("SELECT p FROM Property p WHERE " +
            "p.city = :city AND p.propertyType = :type AND " +
            "p.price BETWEEN :minPrice AND :maxPrice AND " +
            "p.id != :excludeId AND p.status = 'APPROVED' " +
            "ORDER BY p.createdAt DESC")
    List<Property> findSimilarProperties(
            @Param("city") String city,
            @Param("type") PropertyType type,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("excludeId") UUID excludeId
    );

    // Analytics methods
    long countByStatus(PropertyStatus status);

    @Query("SELECT p.propertyType, COUNT(p) FROM Property p GROUP BY p.propertyType")
    List<Object[]> countByPropertyType();

    @Query("SELECT p.city, COUNT(p) FROM Property p GROUP BY p.city ORDER BY COUNT(p) DESC")
    List<Object[]> countByCity();


    List<Property> findTop10ByOrderByViewCountDesc();
    List<Property> findTop10ByOrderByFavoriteCountDesc();

    @Query("SELECT COUNT(DISTINCT p.city) FROM Property p")
    Long findDistinctCitiesCount();

    List<Property> findAllByStatusAndDeletedAtIsNull(PropertyStatus status);
}
