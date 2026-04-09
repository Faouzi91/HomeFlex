package com.homeflex.features.property.api.v1;

import com.homeflex.features.property.domain.entity.PropertyAvailability;
import com.homeflex.features.property.service.PropertyAvailabilityService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties/{propertyId}/availability")
@RequiredArgsConstructor
public class PropertyAvailabilityController {

    private final PropertyAvailabilityService availabilityService;
    private final com.homeflex.core.service.UserService userService;

    /// Public — anyone browsing the listing should see which dates are taken.
    /// Returns one row per unavailable date in the range.
    @GetMapping
    public ResponseEntity<List<AvailabilityDto>> getRange(
            @PathVariable UUID propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        var rows = availabilityService.getRange(propertyId, start, end);
        return ResponseEntity.ok(rows.stream().map(AvailabilityDto::from).toList());
    }

    @PostMapping("/block")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<Void> block(
            @PathVariable UUID propertyId,
            @RequestBody @NotNull RangeRequest body,
            @AuthenticationPrincipal UserDetails principal) {
        var landlordId = userService.getUserByEmail(principal.getUsername()).getId();
        availabilityService.blockRange(propertyId, landlordId, body.start(), body.end());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/unblock")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<Void> unblock(
            @PathVariable UUID propertyId,
            @RequestBody @NotNull RangeRequest body,
            @AuthenticationPrincipal UserDetails principal) {
        var landlordId = userService.getUserByEmail(principal.getUsername()).getId();
        availabilityService.unblockRange(propertyId, landlordId, body.start(), body.end());
        return ResponseEntity.noContent().build();
    }

    public record RangeRequest(LocalDate start, LocalDate end) {}

    public record AvailabilityDto(LocalDate date, String status, UUID bookingId) {
        static AvailabilityDto from(PropertyAvailability a) {
            return new AvailabilityDto(a.getDate(), a.getStatus().name(), a.getBookingId());
        }
    }
}
