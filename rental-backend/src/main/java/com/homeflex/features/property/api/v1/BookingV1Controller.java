package com.homeflex.features.property.api.v1;

import com.homeflex.features.property.service.BookingService;
import com.homeflex.features.property.dto.response.BookingDto;
import com.homeflex.core.dto.common.ApiListResponse;
import com.homeflex.features.property.dto.request.BookingCreateRequest;
import com.homeflex.features.property.dto.request.BookingResponseRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingV1Controller {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<BookingDto> createBooking(
            @Valid @RequestBody BookingCreateRequest request,
            Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getName());
        BookingDto created = bookingService.createBooking(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<ApiListResponse<BookingDto>> getMyBookings(Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(new ApiListResponse<>(bookingService.getBookingsByTenant(tenantId)));
    }

    @GetMapping("/property/{propertyId}")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<ApiListResponse<BookingDto>> getPropertyBookings(
            @PathVariable UUID propertyId,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(new ApiListResponse<>(
                bookingService.getBookingsByProperty(propertyId, landlordId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable UUID id, Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(bookingService.getBookingById(id, userId));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<BookingDto> approveBooking(
            @PathVariable UUID id,
            @RequestBody(required = false) BookingResponseRequest request,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        String response = request != null ? request.message() : null;
        return ResponseEntity.ok(bookingService.approveBooking(id, landlordId, response));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<BookingDto> rejectBooking(
            @PathVariable UUID id,
            @RequestBody BookingResponseRequest request,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(bookingService.rejectBooking(id, landlordId, request.message()));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<BookingDto> cancelBooking(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(bookingService.cancelBooking(id, tenantId));
    }

    @PostMapping("/{id}/modify")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<BookingDto> requestModification(
            @PathVariable UUID id,
            @Valid @RequestBody com.homeflex.features.property.dto.request.BookingModificationRequest request,
            Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(bookingService.requestModification(
                id, request.startDate(), request.endDate(), request.reason(), tenantId
        ));
    }

    @PatchMapping("/{id}/modify/approve")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<BookingDto> approveModification(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(bookingService.approveModification(id, landlordId));
    }

    @PatchMapping("/{id}/modify/reject")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<BookingDto> rejectModification(
            @PathVariable UUID id,
            @RequestBody(required = false) BookingResponseRequest request,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        String reason = request != null ? request.message() : null;
        return ResponseEntity.ok(bookingService.rejectModification(id, reason, landlordId));
    }
}
