package com.homeflex.core.domain.repository;

import com.homeflex.core.domain.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, UUID> {
    Optional<Agency> findByCustomDomain(String customDomain);
    Optional<Agency> findByEmail(String email);
}
