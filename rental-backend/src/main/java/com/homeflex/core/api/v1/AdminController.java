package com.homeflex.core.api.v1;

import com.homeflex.features.property.dto.response.AnalyticsDto;
import com.homeflex.features.property.dto.response.PropertyDto;
import com.homeflex.features.property.dto.response.ReportDto;
import com.homeflex.core.dto.response.UserDto;
import com.homeflex.core.dto.common.ApiPageResponse;
import com.homeflex.features.property.dto.request.RejectReasonRequest;
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
            @RequestBody RejectReasonRequest request) {
        return ResponseEntity.ok(adminService.rejectProperty(id, request.reason()));
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
}
