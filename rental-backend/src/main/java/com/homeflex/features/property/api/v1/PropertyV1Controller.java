package com.homeflex.features.property.api.v1;

import com.homeflex.features.property.service.PropertySearchService;
import com.homeflex.features.property.service.PropertyService;
import com.homeflex.features.property.dto.response.PropertyDto;
import com.homeflex.features.property.dto.response.PropertySearchParams;
import com.homeflex.features.property.dto.response.ReportDto;
import com.homeflex.core.dto.common.ApiListResponse;
import com.homeflex.core.dto.common.ApiPageResponse;
import com.homeflex.features.property.dto.request.PropertyCreateRequest;
import com.homeflex.features.property.dto.request.PropertyUpdateRequest;
import com.homeflex.features.property.dto.request.ReportListingRequest;
import com.homeflex.core.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final PropertyService propertyService;
    private final PropertySearchService propertySearchService;
    private final AdminService adminService;

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        return ResponseEntity.ok(propertyService.getCities());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiPageResponse<PropertyDto>> searchProperties(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) Integer bathrooms,
            @RequestParam(required = false) List<String> amenityIds,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            Pageable pageable) {
        
        return ResponseEntity.ok(propertySearchService.search(
                q, propertyType, city, minPrice, maxPrice, bedrooms, bathrooms, amenityIds, lat, lng, pageable
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDto> getPropertyById(@PathVariable UUID id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<PropertyDto> createProperty(
            @Valid @RequestPart("property") PropertyCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "videos", required = false) List<MultipartFile> videos,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        PropertyDto created = propertyService.createProperty(request, images, videos, landlordId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/json")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<PropertyDto> createPropertyJson(
            @Valid @RequestBody PropertyCreateRequest request,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        PropertyDto created = propertyService.createProperty(request, null, null, landlordId);
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
        return ResponseEntity.ok(propertyService.updateProperty(id, request, images, videos, landlordId));
    }

    @PutMapping("/{id}/json")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<PropertyDto> updatePropertyJson(
            @PathVariable UUID id,
            @Valid @RequestBody PropertyUpdateRequest request,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(propertyService.updateProperty(id, request, null, null, landlordId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<Void> deleteProperty(@PathVariable UUID id, Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        propertyService.deleteProperty(id, landlordId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-properties")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<ApiListResponse<PropertyDto>> getMyProperties(Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(new ApiListResponse<>(propertyService.getPropertiesByLandlord(landlordId)));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable UUID id) {
        propertyService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<PropertyDto> uploadImages(
            @PathVariable UUID id,
            @RequestPart("images") List<MultipartFile> images,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(propertyService.addImages(id, images, landlordId));
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<ApiListResponse<PropertyDto>> getSimilarProperties(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiListResponse<>(propertyService.getSimilarProperties(id)));
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
    public ResponseEntity<ApiListResponse<ReportDto>> getReportsByProperty(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiListResponse<>(adminService.getReportsByProperty(id)));
    }

    @GetMapping("/compare")
    public ResponseEntity<ApiListResponse<PropertyDto>> compareProperties(@RequestParam List<UUID> ids) {
        return ResponseEntity.ok(new ApiListResponse<>(propertyService.getPropertiesByIds(ids)));
    }

    /** Landlord submits a DRAFT property for admin review. */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<PropertyDto> submitForReview(@PathVariable UUID id, Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(propertyService.submitForReview(id, landlordId));
    }
}
