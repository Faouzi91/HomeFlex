package com.homeflex.core.api.v1;

import com.homeflex.core.service.EscrowService;
import com.homeflex.core.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payouts")
@RequiredArgsConstructor
public class PayoutController {

    private final EscrowService escrowService;
    private final PaymentService paymentService;

    /**
     * Returns the landlord's payout summary: available balance, pending balance,
     * funds held in escrow, and total lifetime earnings.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<EscrowService.PayoutSummary> getPayoutSummary(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(escrowService.getPayoutSummary(userId));
    }

    /**
     * Creates or retrieves a Stripe Express connected account for the landlord
     * and returns an onboarding link.
     */
    @PostMapping("/connect/onboard")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<PaymentService.ConnectOnboardingResponse> onboardConnect(
            @Valid @RequestBody ConnectOnboardRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        PaymentService.ConnectOnboardingResponse response =
                paymentService.createConnectedAccount(userId, request.refreshUrl(), request.returnUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public record ConnectOnboardRequest(
            @NotBlank String refreshUrl,
            @NotBlank String returnUrl
    ) {}
}
