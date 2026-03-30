package com.homeflex.core.api.v1;

import com.homeflex.core.service.KycService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    /**
     * Create a Stripe Identity Verification Session.
     * Returns the session ID and client_secret for the frontend to mount
     * the Stripe Identity verification UI.
     */
    @PostMapping("/session")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<KycService.KycSessionResponse> createSession(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        KycService.KycSessionResponse response = kycService.createVerificationSession(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get the current KYC verification status for the authenticated user.
     */
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<KycService.KycStatusResponse> getStatus(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(kycService.getStatus(userId));
    }
}
