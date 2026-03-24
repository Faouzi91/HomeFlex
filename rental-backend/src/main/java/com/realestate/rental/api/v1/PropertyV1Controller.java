package com.realestate.rental.api.v1;

import com.realestate.rental.application.property.PropertyApplicationService;
import com.realestate.rental.dto.PropertyDto;
import com.realestate.rental.dto.PropertySearchParams;
import com.realestate.rental.dto.ReportDto;
import com.realestate.rental.dto.request.PropertyCreateRequest;
import com.realestate.rental.dto.request.PropertyUpdateRequest;
import com.realestate.rental.dto.request.ReportListingRequest;
import com.realestate.rental.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
public class PropertyV1Controller {

    private final PropertyApplicationService propertyApplicationService;
    private final AdminService adminService;

    @GetMapping("/search")
    public ResponseEntity<Page<PropertyDto>> searchProperties(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) Integer bathrooms,
            @RequestParam(required = false) List<String> amenities,
            Pageable pageable) {
        PropertySearchParams params = PropertySearchParams.builder()
                .city(city)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .propertyType(propertyType)
                .bedrooms(bedrooms)
                .bathrooms(bathrooms)
                .amenities(amenities)
                .build();
        return ResponseEntity.ok(propertyApplicationService.searchProperties(params, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDto> getPropertyById(@PathVariable UUID id) {
        return ResponseEntity.ok(propertyApplicationService.getPropertyById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<PropertyDto> createProperty(
            @Valid @RequestPart("property") PropertyCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "videos", required = false) List<MultipartFile> videos,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        PropertyDto created = propertyApplicationService.createProperty(request, images, videos, landlordId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/json")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<PropertyDto> createPropertyJson(
            @Valid @RequestBody PropertyCreateRequest request,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        PropertyDto created = propertyApplicationService.createPropertyJson(request, landlordId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<PropertyDto> updateProperty(
            @PathVariable UUID id,
            @Valid @RequestPart("property") PropertyUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "videos", required = false) List<MultipartFile> videos,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(propertyApplicationService.updateProperty(id, request, images, videos, landlordId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id, Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        propertyApplicationService.deleteProperty(id, landlordId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-properties")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<List<PropertyDto>> getMyProperties(Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(propertyApplicationService.getPropertiesByLandlord(landlordId));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable UUID id) {
        propertyApplicationService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<List<PropertyDto>> getSimilarProperties(@PathVariable UUID id) {
        return ResponseEntity.ok(propertyApplicationService.getSimilarProperties(id));
    }

    @PostMapping("/{id}/report")
    @PreAuthorize("hasAnyRole('TENANT', 'LANDLORD', 'ADMIN')")
    public ResponseEntity<ReportDto> reportProperty(
            @PathVariable UUID id,
            @Valid @RequestBody ReportListingRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        ReportDto report = adminService.createReport(id, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @GetMapping("/{id}/reports")
    @PreAuthorize("hasAnyRole('TENANT', 'LANDLORD', 'ADMIN')")
    public ResponseEntity<List<ReportDto>> getReportsByProperty(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getReportsByProperty(id));
    }
}
