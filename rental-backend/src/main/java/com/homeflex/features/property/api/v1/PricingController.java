package com.homeflex.features.property.api.v1;

import com.homeflex.features.property.dto.response.PricingRecommendationResponse;
import com.homeflex.features.property.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties/{propertyId}/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @GetMapping("/recommendation")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<PricingRecommendationResponse> getPricingRecommendation(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(pricingService.getPricingRecommendation(propertyId));
    }
}
