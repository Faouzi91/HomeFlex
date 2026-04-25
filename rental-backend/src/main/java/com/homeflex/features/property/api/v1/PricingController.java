package com.homeflex.features.property.api.v1;

import com.homeflex.features.property.dto.request.PricingRuleCreateRequest;
import com.homeflex.features.property.dto.response.PricingRecommendationResponse;
import com.homeflex.features.property.dto.response.PricingRuleDto;
import com.homeflex.features.property.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties/{propertyId}/pricing")
@RequiredArgsConstructor
public class PricingController {

    private final PricingService pricingService;

    @GetMapping("/recommendation")
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_ADMIN')")
    public ResponseEntity<PricingRecommendationResponse> getRecommendation(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(pricingService.getPricingRecommendation(propertyId));
    }

    @GetMapping("/rules")
    @PreAuthorize("hasAnyRole('ROLE_LANDLORD', 'ROLE_ADMIN')")
    public ResponseEntity<List<PricingRuleDto>> getRules(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(pricingService.getRules(propertyId));
    }

    @PostMapping("/rules")
    @PreAuthorize("hasAuthority('PROPERTY_UPDATE')")
    public ResponseEntity<PricingRuleDto> createRule(
            @PathVariable UUID propertyId,
            @Valid @RequestBody PricingRuleCreateRequest request) {
        PricingRuleDto created = pricingService.createRule(propertyId, request);
        return ResponseEntity
                .created(URI.create("/api/v1/properties/" + propertyId + "/pricing/rules/" + created.id()))
                .body(created);
    }

    @DeleteMapping("/rules/{ruleId}")
    @PreAuthorize("hasAuthority('PROPERTY_UPDATE')")
    public ResponseEntity<Void> deleteRule(
            @PathVariable UUID propertyId,
            @PathVariable UUID ruleId) {
        pricingService.deleteRule(propertyId, ruleId);
        return ResponseEntity.noContent().build();
    }
}
