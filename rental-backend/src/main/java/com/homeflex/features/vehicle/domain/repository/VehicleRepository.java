package com.homeflex.features.vehicle.domain.repository;

import com.homeflex.features.vehicle.domain.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    @Query(value = """
            SELECT DISTINCT v.* FROM vehicles.vehicles v
            LEFT JOIN vehicles.vehicle_images i ON v.id = i.vehicle_id
            WHERE v.deleted_at IS NULL
              AND (CAST(:brand AS TEXT) IS NULL OR LOWER(v.brand) = LOWER(CAST(:brand AS TEXT)))
              AND (CAST(:model AS TEXT) IS NULL OR LOWER(v.model) = LOWER(CAST(:model AS TEXT)))
              AND (CAST(:city AS TEXT) IS NULL OR LOWER(v.pickup_city) = LOWER(CAST(:city AS TEXT)))
              AND (CAST(:transmission AS TEXT) IS NULL OR v.transmission = CAST(:transmission AS TEXT))
              AND (CAST(:fuelType AS TEXT) IS NULL OR v.fuel_type = CAST(:fuelType AS TEXT))
              AND (CAST(:status AS TEXT) IS NULL OR v.status = CAST(:status AS TEXT))
              AND (CAST(:minPrice AS NUMERIC) IS NULL OR v.daily_price >= CAST(:minPrice AS NUMERIC))
              AND (CAST(:maxPrice AS NUMERIC) IS NULL OR v.daily_price <= CAST(:maxPrice AS NUMERIC))
            ORDER BY v.created_at ASC
            """,
            countQuery = """
            SELECT COUNT(*) FROM vehicles.vehicles v
            WHERE v.deleted_at IS NULL
              AND (CAST(:brand AS TEXT) IS NULL OR LOWER(v.brand) = LOWER(CAST(:brand AS TEXT)))
              AND (CAST(:model AS TEXT) IS NULL OR LOWER(v.model) = LOWER(CAST(:model AS TEXT)))
              AND (CAST(:city AS TEXT) IS NULL OR LOWER(v.pickup_city) = LOWER(CAST(:city AS TEXT)))
              AND (CAST(:transmission AS TEXT) IS NULL OR v.transmission = CAST(:transmission AS TEXT))
              AND (CAST(:fuelType AS TEXT) IS NULL OR v.fuel_type = CAST(:fuelType AS TEXT))
              AND (CAST(:status AS TEXT) IS NULL OR v.status = CAST(:status AS TEXT))
              AND (CAST(:minPrice AS NUMERIC) IS NULL OR v.daily_price >= CAST(:minPrice AS NUMERIC))
              AND (CAST(:maxPrice AS NUMERIC) IS NULL OR v.daily_price <= CAST(:maxPrice AS NUMERIC))
            """,
            nativeQuery = true)
    Page<Vehicle> search(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("city") String city,
            @Param("transmission") String transmission,
            @Param("fuelType") String fuelType,
            @Param("status") String status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query("SELECT v FROM Vehicle v WHERE v.ownerId = :ownerId AND v.deletedAt IS NULL")
    Page<Vehicle> findByOwnerId(@Param("ownerId") UUID ownerId, Pageable pageable);

    @Query(value = "SELECT DISTINCT v.pickup_city FROM vehicles.vehicles v WHERE v.deleted_at IS NULL AND v.pickup_city IS NOT NULL ORDER BY v.pickup_city", nativeQuery = true)
    List<String> findDistinctPickupCities();
}
