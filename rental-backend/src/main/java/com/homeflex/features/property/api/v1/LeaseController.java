package com.homeflex.features.property.api.v1;

import com.homeflex.core.domain.entity.User;
import com.homeflex.features.property.domain.entity.PropertyLease;
import com.homeflex.features.property.service.LeaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leases")
@RequiredArgsConstructor
public class LeaseController {

    private final LeaseService leaseService;
    private final com.homeflex.core.service.UserService userService;

    @GetMapping("/my")
    public ResponseEntity<List<LeaseDto>> getMyLeases(@AuthenticationPrincipal UserDetails principal) {
        User user = userService.getUserByEmail(principal.getUsername());
        var leases = leaseService.getMyLeases(user.getId());
        return ResponseEntity.ok(leases.stream().map(LeaseDto::from).toList());
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<LeaseDto> getLeaseByBooking(@PathVariable UUID bookingId) {
        var lease = leaseService.getLeaseByBooking(bookingId);
        return ResponseEntity.ok(LeaseDto.from(lease));
    }

    @PostMapping("/booking/{bookingId}/generate")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<LeaseDto> generateLease(@PathVariable UUID bookingId) {
        var lease = leaseService.generateLease(bookingId);
        return ResponseEntity.ok(LeaseDto.from(lease));
    }

    @PostMapping("/{leaseId}/sign")
    public ResponseEntity<LeaseDto> signLease(
            @PathVariable UUID leaseId,
            @AuthenticationPrincipal UserDetails principal) {
        User user = userService.getUserByEmail(principal.getUsername());
        var lease = leaseService.signLease(leaseId, user.getId());
        return ResponseEntity.ok(LeaseDto.from(lease));
    }

    @PostMapping("/property/{propertyId}/template")
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<LeaseDto> uploadTemplate(
            @PathVariable UUID propertyId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails principal) {
        User user = userService.getUserByEmail(principal.getUsername());
        var lease = leaseService.uploadLeaseTemplate(propertyId, file, user.getId());
        return ResponseEntity.ok(LeaseDto.from(lease));
    }

    public record LeaseDto(
            UUID id,
            UUID propertyId,
            UUID bookingId,
            UUID landlordId,
            UUID tenantId,
            String leaseUrl,
            String status,
            String signedAt,
            String createdAt) {
        static LeaseDto from(PropertyLease l) {
            return new LeaseDto(
                    l.getId(),
                    l.getProperty().getId(),
                    l.getBooking() != null ? l.getBooking().getId() : null,
                    l.getLandlord().getId(),
                    l.getTenant() != null ? l.getTenant().getId() : null,
                    l.getLeaseUrl(),
                    l.getStatus(),
                    l.getSignedAt() != null ? l.getSignedAt().toString() : null,
                    l.getCreatedAt().toString()
            );
        }
    }
}
