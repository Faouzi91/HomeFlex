package com.homeflex.features.vehicle.domain.repository;

import com.homeflex.features.vehicle.domain.entity.ConditionReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConditionReportRepository extends JpaRepository<ConditionReport, UUID> {

    List<ConditionReport> findByVehicleIdOrderByCreatedAtDesc(UUID vehicleId);

    List<ConditionReport> findByBookingIdOrderByCreatedAtDesc(UUID bookingId);
}
