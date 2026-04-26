package com.homeflex.features.property.api.v1;

import com.homeflex.core.dto.common.ApiListResponse;
import com.homeflex.features.property.dto.request.PropertyUnitRequest;
import com.homeflex.features.property.dto.response.PropertyUnitDto;
import com.homeflex.features.property.service.PropertyUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties/{propertyId}/room-types/{roomTypeId}/units")
@RequiredArgsConstructor
public class PropertyUnitController {

    private final PropertyUnitService unitService;

    @GetMapping
    public ResponseEntity<ApiListResponse<PropertyUnitDto>> listUnits(
            @PathVariable UUID propertyId,
            @PathVariable UUID roomTypeId) {
        return ResponseEntity.ok(new ApiListResponse<>(unitService.listUnits(propertyId, roomTypeId)));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiListResponse<PropertyUnitDto>> listAvailable(
            @PathVariable UUID propertyId,
            @PathVariable UUID roomTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(new ApiListResponse<>(
                unitService.listAvailable(propertyId, roomTypeId, startDate, endDate)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PROPERTY_UPDATE')")
    public ResponseEntity<PropertyUnitDto> createUnit(
            @PathVariable UUID propertyId,
            @PathVariable UUID roomTypeId,
            @Valid @RequestBody PropertyUnitRequest request,
            Authentication auth) {
        UUID ownerId = UUID.fromString(auth.getName());
        PropertyUnitDto created = unitService.createUnit(propertyId, roomTypeId, request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{unitId}")
    @PreAuthorize("hasAuthority('PROPERTY_UPDATE')")
    public ResponseEntity<PropertyUnitDto> updateUnit(
            @PathVariable UUID propertyId,
            @PathVariable UUID roomTypeId,
            @PathVariable UUID unitId,
            @Valid @RequestBody PropertyUnitRequest request,
            Authentication auth) {
        UUID ownerId = UUID.fromString(auth.getName());
        return ResponseEntity.ok(unitService.updateUnit(propertyId, roomTypeId, unitId, request, ownerId));
    }

    @DeleteMapping("/{unitId}")
    @PreAuthorize("hasAuthority('PROPERTY_UPDATE')")
    public ResponseEntity<Void> deleteUnit(
            @PathVariable UUID propertyId,
            @PathVariable UUID roomTypeId,
            @PathVariable UUID unitId,
            Authentication auth) {
        UUID ownerId = UUID.fromString(auth.getName());
        unitService.deleteUnit(propertyId, roomTypeId, unitId, ownerId);
        return ResponseEntity.noContent().build();
    }
}
