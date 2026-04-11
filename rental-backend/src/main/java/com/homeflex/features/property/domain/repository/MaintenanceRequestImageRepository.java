package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.MaintenanceRequestImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MaintenanceRequestImageRepository extends JpaRepository<MaintenanceRequestImage, UUID> {
}
