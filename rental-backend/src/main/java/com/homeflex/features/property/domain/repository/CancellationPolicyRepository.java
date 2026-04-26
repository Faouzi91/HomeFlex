package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.CancellationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CancellationPolicyRepository extends JpaRepository<CancellationPolicy, UUID> {
    Optional<CancellationPolicy> findByCode(String code);
    boolean existsByCode(String code);
}
