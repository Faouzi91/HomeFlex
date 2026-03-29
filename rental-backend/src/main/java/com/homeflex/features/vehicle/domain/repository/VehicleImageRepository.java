package com.homeflex.features.vehicle.domain.repository;

import com.homeflex.features.vehicle.domain.entity.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleImageRepository extends JpaRepository<VehicleImage, UUID> {
}
