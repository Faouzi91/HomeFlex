package com.realestate.rental.repository;

import com.realestate.rental.utils.entity.Property;
import com.realestate.rental.utils.enumeration.PropertyStatus;
import com.realestate.rental.utils.enumeration.PropertyType;
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

    @Query("SELECT p.propertyType as type, COUNT(p) as count FROM Property p " +
            "GROUP BY p.propertyType")
    Map<String, Long> countByPropertyType();

    @Query("SELECT p.city as city, COUNT(p) as count FROM Property p " +
            "GROUP BY p.city ORDER BY count DESC")
    Map<String, Long> countByCity();

    List<Property> findTop10ByOrderByViewCountDesc();
    List<Property> findTop10ByOrderByFavoriteCountDesc();

    @Query("SELECT COUNT(DISTINCT p.city) FROM Property p")
    Long findDistinctCitiesCount();

}
