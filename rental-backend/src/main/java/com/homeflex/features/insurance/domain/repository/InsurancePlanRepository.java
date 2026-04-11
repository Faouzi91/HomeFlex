package com.homeflex.features.insurance.domain.repository;

import com.homeflex.features.insurance.domain.entity.InsurancePlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InsurancePlanRepository extends JpaRepository<InsurancePlan, UUID> {
    List<InsurancePlan> findByType(String type);
}
