package com.homeflex.features.property.api.v1;

import com.homeflex.features.property.dto.request.MaintenanceRequestCreateRequest;
import com.homeflex.features.property.dto.request.MaintenanceStatusUpdateRequest;
import com.homeflex.features.property.dto.response.MaintenanceRequestResponse;
import com.homeflex.features.property.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<MaintenanceRequestResponse> createRequest(
            @Valid @RequestBody MaintenanceRequestCreateRequest requestDto,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(maintenanceService.createRequest(userId, requestDto));
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<Void> uploadImages(
            @PathVariable UUID id,
            @RequestParam("files") List<MultipartFile> files,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        maintenanceService.uploadImages(id, userId, files);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<MaintenanceRequestResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody MaintenanceStatusUpdateRequest updateDto,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(maintenanceService.updateStatus(id, userId, updateDto));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<MaintenanceRequestResponse>> getMyRequests(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(maintenanceService.getTenantRequests(userId));
    }

    @GetMapping("/landlord")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<MaintenanceRequestResponse>> getLandlordRequests(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(maintenanceService.getLandlordRequests(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceRequestResponse> getRequest(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(maintenanceService.getRequest(id, userId));
    }
}
