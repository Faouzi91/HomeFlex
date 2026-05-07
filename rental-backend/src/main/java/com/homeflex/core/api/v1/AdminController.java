package com.homeflex.core.api.v1;

import com.homeflex.features.property.dto.response.AnalyticsDto;
import com.homeflex.features.property.dto.response.PropertyDto;
import com.homeflex.features.property.dto.response.ReportDto;
import com.homeflex.core.dto.response.UserDto;
import com.homeflex.core.dto.common.ApiPageResponse;
import com.homeflex.features.property.dto.request.RejectReasonRequest;
import com.homeflex.features.property.dto.request.RoleChangeRequest;
import com.homeflex.core.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final com.homeflex.features.property.service.PropertyService propertyService;

    @GetMapping("/properties/pending")
    public ResponseEntity<ApiPageResponse<PropertyDto>> getPendingProperties(Pageable pageable) {
        return ResponseEntity.ok(ApiPageResponse.from(adminService.getPendingProperties(pageable)));
    }

    @PatchMapping("/properties/{id}/approve")
    public ResponseEntity<PropertyDto> approveProperty(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.approveProperty(id));
    }

    @PatchMapping("/properties/{id}/reject")
    public ResponseEntity<PropertyDto> rejectProperty(
            @PathVariable UUID id,
            @RequestBody @jakarta.validation.Valid RejectReasonRequest request) {
        return ResponseEntity.ok(adminService.rejectProperty(id, request.reason()));
    }

    @PatchMapping("/properties/{id}/suspend")
    public ResponseEntity<PropertyDto> suspendProperty(
            @PathVariable UUID id,
            @RequestBody @jakarta.validation.Valid RejectReasonRequest request) {
        return ResponseEntity.ok(adminService.suspendProperty(id, request.reason()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiPageResponse<UserDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(ApiPageResponse.from(adminService.getAllUsers(pageable)));
    }

    @PatchMapping("/users/{id}/suspend")
    public ResponseEntity<UserDto> suspendUser(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.suspendUser(id));
    }

    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<UserDto> activateUser(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.activateUser(id));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserDto> changeUserRole(
            @PathVariable UUID id,
            @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid RoleChangeRequest request) {
        return ResponseEntity.ok(adminService.changeUserRole(id, request.role()));
    }

    @GetMapping("/analytics")
    public ResponseEntity<AnalyticsDto> getAnalytics() {
        return ResponseEntity.ok(adminService.getAnalytics());
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiPageResponse<ReportDto>> getReports(Pageable pageable) {
        return ResponseEntity.ok(ApiPageResponse.from(adminService.getReports(pageable)));
    }

    @PatchMapping("/reports/{id}/resolve")
    public ResponseEntity<ReportDto> resolveReport(
            @PathVariable UUID id,
            @RequestBody(required = false) RejectReasonRequest request,
            org.springframework.security.core.Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getName());
        String notes = request != null ? request.reason() : null;
        return ResponseEntity.ok(adminService.resolveReport(id, adminId, notes));
    }

    // ── System Configuration ────────────────────────────────────────────

    @GetMapping("/configs")
    public ResponseEntity<java.util.List<com.homeflex.core.domain.entity.SystemConfig>> getConfigs() {
        return ResponseEntity.ok(adminService.getAllConfigs());
    }

    @PatchMapping("/configs/{key}")
    public ResponseEntity<com.homeflex.core.domain.entity.SystemConfig> updateConfig(
            @PathVariable String key,
            @RequestParam String value) {
        return ResponseEntity.ok(adminService.updateConfig(key, value));
    }

    // ── Amenity Management ──────────────────────────────────────────────

    @GetMapping("/amenities")
    public ResponseEntity<java.util.List<com.homeflex.features.property.domain.entity.Amenity>> listAmenities() {
        return ResponseEntity.ok(adminService.listAmenities());
    }

    @PostMapping("/amenities")
    public ResponseEntity<com.homeflex.features.property.domain.entity.Amenity> createAmenity(
            @RequestBody @jakarta.validation.Valid
            com.homeflex.features.property.dto.request.AmenityRequest request) {
        return ResponseEntity.ok(adminService.createAmenity(request));
    }

    @PutMapping("/amenities/{id}")
    public ResponseEntity<com.homeflex.features.property.domain.entity.Amenity> updateAmenity(
            @PathVariable UUID id,
            @RequestBody @jakarta.validation.Valid
            com.homeflex.features.property.dto.request.AmenityRequest request) {
        return ResponseEntity.ok(adminService.updateAmenity(id, request));
    }

    @DeleteMapping("/amenities/{id}")
    public ResponseEntity<Void> deleteAmenity(@PathVariable UUID id) {
        adminService.deleteAmenity(id);
        return ResponseEntity.noContent().build();
    }

    // ── Search Reindex ─────────────────────────────────────────────────

    @PostMapping("/properties/reindex")
    public ResponseEntity<java.util.Map<String, Object>> reindexProperties() {
        int count = propertyService.reindexAll();
        return ResponseEntity.ok(java.util.Map.of("enqueued", count));
    }

    // ── Pricing Rules (cross-property) ─────────────────────────────────

    @GetMapping("/pricing-rules")
    public ResponseEntity<java.util.List<com.homeflex.features.property.dto.response.AdminPricingRuleDto>> listAllPricingRules() {
        return ResponseEntity.ok(adminService.listAllPricingRules());
    }

    @DeleteMapping("/pricing-rules/{ruleId}")
    public ResponseEntity<Void> deletePricingRule(@PathVariable UUID ruleId) {
        adminService.deletePricingRule(ruleId);
        return ResponseEntity.noContent().build();
    }

    // ── Cancellation Policies ──────────────────────────────────────────

    @GetMapping("/cancellation-policies")
    public ResponseEntity<java.util.List<com.homeflex.features.property.domain.entity.CancellationPolicy>> listCancellationPolicies() {
        return ResponseEntity.ok(adminService.listCancellationPolicies());
    }

    @PostMapping("/cancellation-policies")
    public ResponseEntity<com.homeflex.features.property.domain.entity.CancellationPolicy> createCancellationPolicy(
            @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid
            com.homeflex.features.property.dto.request.CancellationPolicyRequest request) {
        return ResponseEntity.ok(adminService.createCancellationPolicy(request));
    }

    @PutMapping("/cancellation-policies/{id}")
    public ResponseEntity<com.homeflex.features.property.domain.entity.CancellationPolicy> updateCancellationPolicy(
            @PathVariable UUID id,
            @org.springframework.web.bind.annotation.RequestBody @jakarta.validation.Valid
            com.homeflex.features.property.dto.request.CancellationPolicyRequest request) {
        return ResponseEntity.ok(adminService.updateCancellationPolicy(id, request));
    }

    @DeleteMapping("/cancellation-policies/{id}")
    public ResponseEntity<Void> deleteCancellationPolicy(@PathVariable UUID id) {
        adminService.deleteCancellationPolicy(id);
        return ResponseEntity.noContent().build();
    }
}
