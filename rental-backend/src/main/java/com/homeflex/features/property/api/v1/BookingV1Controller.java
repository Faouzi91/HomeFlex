package com.homeflex.features.property.api.v1;

import com.homeflex.core.security.Permissions;
import com.homeflex.features.property.service.BookingService;
import com.homeflex.features.property.dto.response.BookingDto;
import com.homeflex.features.property.dto.response.PaymentInitiationResponse;
import com.homeflex.features.property.domain.entity.BookingAuditLog;
import com.homeflex.core.dto.common.ApiListResponse;
import com.homeflex.features.property.dto.request.BookingCreateRequest;
import com.homeflex.features.property.dto.request.BookingModificationRequest;
import com.homeflex.features.property.dto.request.BookingResponseRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Booking REST endpoints.
 *
 * Authorization strategy:
 *   hasAuthority(Permissions.X)       — "you hold this permission" (no ownership)
 *                                        used for collection/create endpoints where we
 *                                        don't have a resource ID yet.
 *
 *   hasPermission(#id, 'Type', 'X')   — "you hold this permission AND own this resource"
 *                                        evaluated by HomeFlexPermissionEvaluator →
 *                                        ResourcePermissionService. Used for all
 *                                        single-resource mutations and reads.
 *
 * The service layer contains no ownership checks; security is enforced here.
 * See ResourcePermissionService for the ownership rules.
 */
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingV1Controller {

    private final BookingService bookingService;

    // ── Create & Pay ──────────────────────────────────────────────────────────

    @PostMapping("/draft")
    @PreAuthorize("hasAuthority(T(com.homeflex.core.security.Permissions).BOOKING_CREATE)")
    public ResponseEntity<BookingDto> createDraftBooking(
            @Valid @RequestBody BookingCreateRequest request,
            Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getName());
        BookingDto created = bookingService.createDraftBooking(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasPermission(#id, 'Booking', 'BOOKING_UPDATE')")
    public ResponseEntity<PaymentInitiationResponse> initiatePayment(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(bookingService.initiatePayment(id, tenantId));
    }

    @PostMapping("/{id}/retry-payment")
    @PreAuthorize("hasPermission(#id, 'Booking', 'BOOKING_UPDATE')")
    public ResponseEntity<PaymentInitiationResponse> retryPayment(
            @PathVariable UUID id,
            Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(bookingService.retryPayment(id, tenantId));
    }

    @PostMapping("/{id}/confirm-payment")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Usually called via webhook, keeping here for admin/manual tests
    public ResponseEntity<Void> confirmPaymentManual(@PathVariable UUID id) {
        bookingService.confirmPayment(id);
        return ResponseEntity.ok().build();
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @GetMapping("/my-bookings")
    @PreAuthorize("hasAuthority(T(com.homeflex.core.security.Permissions).BOOKING_READ)")
    public ResponseEntity<ApiListResponse<BookingDto>> getMyBookings(Authentication authentication) {
        UUID tenantId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(new ApiListResponse<>(bookingService.getBookingsByTenant(tenantId)));
    }

    @GetMapping("/property/{propertyId}")
    @PreAuthorize("hasPermission(#propertyId, 'Property', 'BOOKING_READ')")
    public ResponseEntity<ApiListResponse<BookingDto>> getPropertyBookings(
            @PathVariable UUID propertyId) {
        return ResponseEntity.ok(new ApiListResponse<>(bookingService.getBookingsByProperty(propertyId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Booking', 'BOOKING_READ')")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/{id}/audit-log")
    @PreAuthorize("hasPermission(#id, 'Booking', 'BOOKING_READ')")
    public ResponseEntity<ApiListResponse<BookingAuditLog>> getAuditLog(@PathVariable UUID id) {
        return ResponseEntity.ok(new ApiListResponse<>(bookingService.getAuditLog(id)));
    }

    // ── Landlord actions ──────────────────────────────────────────────────────

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasPermission(#id, 'Booking', 'BOOKING_APPROVE')")
    public ResponseEntity<BookingDto> approveBooking(
            @PathVariable UUID id,
            @RequestBody(required = false) BookingResponseRequest request) {
        String response = request != null ? request.message() : null;
        return ResponseEntity.ok(bookingService.approveBooking(id, response));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasPermission(#id, 'Booking', 'BOOKING_APPROVE')")
    public ResponseEntity<BookingDto> rejectBooking(
            @PathVariable UUID id,
            @RequestBody BookingResponseRequest request) {
        return ResponseEntity.ok(bookingService.rejectBooking(id, request.message()));
    }

    @PatchMapping("/{id}/modify/approve")
    @PreAuthorize("hasPermission(#id, 'Booking', 'BOOKING_APPROVE')")
    public ResponseEntity<BookingDto> approveModification(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.approveModification(id));
    }

    @PatchMapping("/{id}/modify/reject")
    @PreAuthorize("hasPermission(#id, 'Booking', 'BOOKING_APPROVE')")
    public ResponseEntity<BookingDto> rejectModification(
            @PathVariable UUID id,
            @RequestBody(required = false) BookingResponseRequest request) {
        String reason = request != null ? request.message() : null;
        return ResponseEntity.ok(bookingService.rejectModification(id, reason));
    }

    // ── Tenant actions ────────────────────────────────────────────────────────

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasPermission(#id, 'Booking', 'BOOKING_CANCEL')")
    public ResponseEntity<BookingDto> cancelBooking(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PatchMapping("/{id}/early-checkout")
    @PreAuthorize("hasPermission(#id, 'Booking', 'BOOKING_CANCEL')")
    public ResponseEntity<BookingDto> earlyCheckout(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.earlyCheckout(id));
    }

    @PostMapping("/{id}/modify")
    @PreAuthorize("hasPermission(#id, 'Booking', 'BOOKING_CANCEL')")
    public ResponseEntity<BookingDto> requestModification(
            @PathVariable UUID id,
            @Valid @RequestBody BookingModificationRequest request) {
        return ResponseEntity.ok(bookingService.requestModification(
                id, request.startDate(), request.endDate(), request.reason()));
    }
}
