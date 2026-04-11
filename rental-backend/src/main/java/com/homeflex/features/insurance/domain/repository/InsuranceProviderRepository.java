package com.homeflex.features.insurance.domain.repository;

import com.homeflex.features.insurance.domain.entity.InsuranceProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InsuranceProviderRepository extends JpaRepository<InsuranceProvider, UUID> {
}
