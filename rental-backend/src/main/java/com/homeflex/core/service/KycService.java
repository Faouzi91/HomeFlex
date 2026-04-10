package com.homeflex.core.service;

import com.homeflex.core.config.AppProperties;
import com.homeflex.core.domain.entity.KycVerification;
import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.KycStatus;
import com.homeflex.core.domain.enums.UserRole;
import com.homeflex.core.domain.repository.KycVerificationRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.stripe.exception.StripeException;
import com.stripe.model.identity.VerificationSession;
import com.stripe.param.identity.VerificationSessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KycService {

    private final KycVerificationRepository kycRepository;
    private final UserRepository userRepository;
    private final AppProperties appProperties;

    /**
     * Creates a Stripe Identity Verification Session for a landlord.
     * Returns the client_secret so the frontend can mount the verification UI.
     */
    @Transactional
    public KycSessionResponse createVerificationSession(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (user.getRole() != UserRole.LANDLORD && user.getRole() != UserRole.ADMIN) {
            throw new DomainException("Only landlords can submit KYC verification");
        }

        if (Boolean.TRUE.equals(user.getIsVerified())) {
            throw new ConflictException("User is already verified");
        }

        // Check for an existing pending session
        if (kycRepository.existsByUserIdAndStatus(userId, KycStatus.PENDING)) {
            throw new ConflictException("A verification session is already pending");
        }

        try {
            VerificationSessionCreateParams params = VerificationSessionCreateParams.builder()
                    .setType(VerificationSessionCreateParams.Type.DOCUMENT)
                    .putMetadata("user_id", userId.toString())
                    .build();

            VerificationSession session = VerificationSession.create(params);

            KycVerification kyc = new KycVerification();
            kyc.setUserId(userId);
            kyc.setStripeSessionId(session.getId());
            kyc.setStatus(KycStatus.PENDING);
            kycRepository.save(kyc);

            log.info("KYC session created: sessionId={}, userId={}", session.getId(), userId);

            return new KycSessionResponse(session.getId(), session.getClientSecret(), appProperties.getStripe().getPublishableKey());
        } catch (StripeException e) {
            log.error("Failed to create Stripe Identity session for user {}", userId, e);
            throw new DomainException("Unable to start identity verification. Please try again later.");
        }
    }

    /**
     * Handles the identity.verification_session.verified event from Stripe.
     */
    @Transactional
    public void handleVerified(String stripeSessionId) {
        KycVerification kyc = kycRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> {
                    log.warn("Received verified event for unknown session: {}", stripeSessionId);
                    return new ResourceNotFoundException("KYC session not found: " + stripeSessionId);
                });

        kyc.setStatus(KycStatus.VERIFIED);
        kyc.setVerifiedAt(LocalDateTime.now());
        kycRepository.save(kyc);

        User user = userRepository.findById(kyc.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + kyc.getUserId()));
        user.setIsVerified(true);
        userRepository.save(user);

        log.info("KYC verified: userId={}, sessionId={}", kyc.getUserId(), stripeSessionId);
    }

    /**
     * Handles the identity.verification_session.requires_input (rejection) event from Stripe.
     */
    @Transactional
    public void handleRejected(String stripeSessionId, String reason) {
        KycVerification kyc = kycRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> {
                    log.warn("Received rejected event for unknown session: {}", stripeSessionId);
                    return new ResourceNotFoundException("KYC session not found: " + stripeSessionId);
                });

        kyc.setStatus(KycStatus.REJECTED);
        kyc.setRejectionReason(reason);
        kycRepository.save(kyc);

        log.info("KYC rejected: userId={}, sessionId={}, reason={}", kyc.getUserId(), stripeSessionId, reason);
    }

    /**
     * Returns the current KYC status for a user.
     */
    @Transactional(readOnly = true)
    public KycStatusResponse getStatus(UUID userId) {
        return kycRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(kyc -> new KycStatusResponse(
                        kyc.getStatus().name(),
                        kyc.getRejectionReason(),
                        kyc.getVerifiedAt(),
                        kyc.getCreatedAt()))
                .orElse(new KycStatusResponse("NOT_STARTED", null, null, null));
    }

    /**
     * Checks whether the given user has completed KYC. Admins bypass this check.
     */
    @Transactional(readOnly = true)
    public void requireVerified(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (user.getRole() == UserRole.ADMIN) {
            return; // Admins bypass KYC
        }

        if (!Boolean.TRUE.equals(user.getIsVerified())) {
            throw new DomainException("KYC verification required before publishing listings. "
                    + "Please complete identity verification at /api/v1/kyc/session.");
        }
    }

    // ── Response records ───────────────────────────────────────────────

    public record KycSessionResponse(String sessionId, String clientSecret) {}

    public record KycStatusResponse(
            String status,
            String rejectionReason,
            LocalDateTime verifiedAt,
            LocalDateTime submittedAt) {}
}
