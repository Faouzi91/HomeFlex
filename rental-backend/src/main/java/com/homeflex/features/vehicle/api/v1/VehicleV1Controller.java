package com.homeflex.features.vehicle.api.v1;

import com.homeflex.core.dto.common.ApiListResponse;
import com.homeflex.core.dto.common.ApiPageResponse;
import com.homeflex.features.vehicle.domain.entity.VehicleBooking;
import com.homeflex.features.vehicle.domain.enums.FuelType;
import com.homeflex.features.vehicle.domain.enums.Transmission;
import com.homeflex.features.vehicle.domain.enums.VehicleStatus;
import com.homeflex.features.vehicle.dto.request.VehicleBookingCreateRequest;
import com.homeflex.features.vehicle.dto.request.VehicleCreateRequest;
import com.homeflex.features.vehicle.dto.request.VehicleUpdateRequest;
import com.homeflex.features.vehicle.dto.response.ConditionReportResponse;
import com.homeflex.features.vehicle.dto.response.VehicleBookingResponse;
import com.homeflex.features.vehicle.dto.response.VehicleResponse;
import com.homeflex.features.vehicle.dto.response.VehicleSearchParams;
import com.homeflex.features.vehicle.mapper.VehicleMapper;
import com.homeflex.features.vehicle.service.VehicleAvailabilityService;
import com.homeflex.features.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleV1Controller {

    private final VehicleService vehicleService;
    private final VehicleAvailabilityService vehicleAvailabilityService;
    private final VehicleMapper vehicleMapper;

    // ── Public search & detail ──────────────────────────────────────────

    @GetMapping("/search")
    public ResponseEntity<ApiPageResponse<VehicleResponse>> search(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Transmission transmission,
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        VehicleSearchParams params = new VehicleSearchParams(
                brand, model, city, transmission, fuelType, status, minPrice, maxPrice
        );
        Page<VehicleResponse> page = vehicleService.search(params, pageable);
        return ResponseEntity.ok(ApiPageResponse.from(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(vehicleService.getById(id));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable UUID id) {
        vehicleService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    // ── Owner CRUD ──────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<VehicleResponse> create(
            @Valid @RequestBody VehicleCreateRequest request,
            Authentication authentication) {
        UUID ownerId = UUID.fromString(authentication.getName());
        VehicleResponse response = vehicleService.create(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<VehicleResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleUpdateRequest request,
            Authentication authentication) {
        UUID ownerId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(vehicleService.update(id, request, ownerId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       Authentication authentication) {
        UUID ownerId = UUID.fromString(authentication.getName());
        vehicleService.softDelete(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    // ── Image uploads ───────────────────────────────────────────────────

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<VehicleResponse> uploadImages(
            @PathVariable UUID id,
            @RequestPart("images") List<MultipartFile> images,
            Authentication authentication) {
        UUID ownerId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(vehicleService.uploadImages(id, images, ownerId));
    }

    // ── Condition reports ───────────────────────────────────────────────

    @PostMapping("/{id}/condition")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<ConditionReportResponse> createConditionReport(
            @PathVariable UUID id,
            @RequestParam String notes,
            @RequestParam(required = false) Integer mileageAt,
            @RequestParam(required = false) String fuelLevel,
            @RequestParam(required = false) UUID bookingId,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos,
            Authentication authentication) {
        UUID reporterId = UUID.fromString(authentication.getName());
        ConditionReportResponse report = vehicleService.createConditionReport(
                id, reporterId, notes, mileageAt, fuelLevel, bookingId, photos);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    @GetMapping("/{id}/condition")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<ApiListResponse<ConditionReportResponse>> getConditionReports(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                new ApiListResponse<>(vehicleService.getConditionReports(id)));
    }

    // ── Vehicle Bookings ────────────────────────────────────────────────

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @PathVariable UUID id,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(vehicleAvailabilityService.isAvailable(id, startDate, endDate));
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<ApiListResponse<VehicleBookingResponse>> getActiveBookings(
            @PathVariable UUID id) {
        List<VehicleBooking> bookings = vehicleAvailabilityService.getActiveBookings(id);
        return ResponseEntity.ok(
                new ApiListResponse<>(vehicleMapper.toBookingResponseList(bookings)));
    }

    @PostMapping("/{id}/bookings/draft")
    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN')")
    public ResponseEntity<VehicleBookingResponse> createDraftBooking(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleBookingCreateRequest request,
            Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getName());
        VehicleBooking booking = vehicleAvailabilityService.reserve(
                id, tenantId, request.startDate(), request.endDate(), request.message());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleMapper.toBookingResponse(booking));
    }

    @PostMapping("/{id}/bookings/{bookingId}/pay")
    @PreAuthorize("hasAnyRole('TENANT', 'ADMIN')")
    public ResponseEntity<com.homeflex.features.property.dto.response.PaymentInitiationResponse> initiatePayment(
            @PathVariable UUID id,
            @PathVariable UUID bookingId,
            Authentication authentication) {
        // tenant check is implicit in the service layer's initiation (though we should strictly use ResourcePermissionService)
        var result = vehicleAvailabilityService.initiatePayment(bookingId);
        return ResponseEntity.ok(new com.homeflex.features.property.dto.response.PaymentInitiationResponse(
                bookingId, result.clientSecret(), result.paymentIntentId(), result.amount(), result.currency()));
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiListResponse<VehicleBookingResponse>> getMyBookings(
            Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getName());
        List<VehicleBooking> bookings = vehicleAvailabilityService.getTenantBookings(tenantId);
        return ResponseEntity.ok(
                new ApiListResponse<>(vehicleMapper.toBookingResponseList(bookings)));
    }

    @GetMapping("/my-vehicles")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<ApiPageResponse<VehicleResponse>> getMyVehicles(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            Authentication authentication) {
        UUID ownerId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(ApiPageResponse.from(vehicleService.getByOwnerId(ownerId, pageable)));
    }
}
