package com.realestate.rental.controller;

import com.realestate.rental.dto.*;
import com.realestate.rental.dto.request.RejectReasonRequest;
import com.realestate.rental.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/properties/pending")
    public ResponseEntity<Page<PropertyDto>> getPendingProperties(Pageable pageable) {
        return ResponseEntity.ok(adminService.getPendingProperties(pageable));
    }

    @PatchMapping("/properties/{id}/approve")
    public ResponseEntity<PropertyDto> approveProperty(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.approveProperty(id));
    }

    @PatchMapping("/properties/{id}/reject")
    public ResponseEntity<PropertyDto> rejectProperty(
            @PathVariable UUID id,
            @RequestBody RejectReasonRequest request) {
        return ResponseEntity.ok(adminService.rejectProperty(id, request.getReason()));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
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
    public ResponseEntity<Page<ReportDto>> getReports(Pageable pageable) {
        return ResponseEntity.ok(adminService.getReports(pageable));
    }

    @PatchMapping("/reports/{id}/resolve")
    public ResponseEntity<ReportDto> resolveReport(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.resolveReport(id));
    }
}
