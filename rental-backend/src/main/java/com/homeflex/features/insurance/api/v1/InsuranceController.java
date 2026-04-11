package com.homeflex.features.insurance.api.v1;

import com.homeflex.features.insurance.domain.entity.InsurancePolicy;
import com.homeflex.features.insurance.dto.response.InsurancePlanResponse;
import com.homeflex.features.insurance.service.InsuranceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/insurance")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;

    @GetMapping("/plans")
    public ResponseEntity<List<InsurancePlanResponse>> getPlans(@RequestParam(defaultValue = "TENANT") String type) {
        return ResponseEntity.ok(insuranceService.getAvailablePlans(type));
    }

    @PostMapping("/purchase")
    public ResponseEntity<InsurancePolicy> purchasePolicy(
            @RequestParam UUID planId,
            @RequestParam UUID bookingId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(insuranceService.purchasePolicy(userId, planId, bookingId));
    }
}
