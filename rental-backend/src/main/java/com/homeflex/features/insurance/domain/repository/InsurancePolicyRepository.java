package com.homeflex.features.insurance.domain.repository;

import com.homeflex.features.insurance.domain.entity.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, UUID> {
    List<InsurancePolicy> findByUserId(UUID userId);
}
