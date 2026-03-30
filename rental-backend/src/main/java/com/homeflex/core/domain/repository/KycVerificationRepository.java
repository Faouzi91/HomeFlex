package com.homeflex.core.domain.repository;

import com.homeflex.core.domain.entity.KycVerification;
import com.homeflex.core.domain.enums.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KycVerificationRepository extends JpaRepository<KycVerification, UUID> {

    Optional<KycVerification> findByStripeSessionId(String stripeSessionId);

    Optional<KycVerification> findTopByUserIdOrderByCreatedAtDesc(UUID userId);

    boolean existsByUserIdAndStatus(UUID userId, KycStatus status);
}
