package com.homeflex.features.vehicle.domain.repository;

import com.homeflex.features.vehicle.domain.entity.Vehicle;
import com.homeflex.features.vehicle.domain.enums.FuelType;
import com.homeflex.features.vehicle.domain.enums.Transmission;
import com.homeflex.features.vehicle.domain.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    @Query("""
            SELECT v FROM Vehicle v
            WHERE v.deletedAt IS NULL
              AND (:brand IS NULL OR LOWER(v.brand) = LOWER(:brand))
              AND (:model IS NULL OR LOWER(v.model) = LOWER(:model))
              AND (:city IS NULL OR LOWER(v.pickupCity) = LOWER(:city))
              AND (:transmission IS NULL OR v.transmission = :transmission)
              AND (:fuelType IS NULL OR v.fuelType = :fuelType)
              AND (:status IS NULL OR v.status = :status)
              AND (:minPrice IS NULL OR v.dailyPrice >= :minPrice)
              AND (:maxPrice IS NULL OR v.dailyPrice <= :maxPrice)
            """)
    Page<Vehicle> search(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("city") String city,
            @Param("transmission") Transmission transmission,
            @Param("fuelType") FuelType fuelType,
            @Param("status") VehicleStatus status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("SELECT v FROM Vehicle v WHERE v.ownerId = :ownerId AND v.deletedAt IS NULL")
    Page<Vehicle> findByOwnerId(@Param("ownerId") UUID ownerId, Pageable pageable);
}
