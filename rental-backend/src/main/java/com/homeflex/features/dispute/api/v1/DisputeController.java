package com.homeflex.features.dispute.api.v1;

import com.homeflex.features.dispute.domain.entity.Dispute;
import com.homeflex.features.dispute.domain.entity.DisputeEvidence;
import com.homeflex.features.dispute.service.DisputeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/disputes")
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping
    public ResponseEntity<Dispute> openDispute(
            @RequestParam UUID bookingId,
            @RequestParam String reason,
            @RequestParam String description,
            Authentication authentication) {
        UUID initiatorId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(disputeService.openDispute(bookingId, initiatorId, reason, description));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Dispute>> getAllDisputes() {
        return ResponseEntity.ok(disputeService.getAllDisputes());
    }

    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Dispute> resolveDispute(
            @PathVariable UUID id,
            @RequestParam String resolutionNotes,
            Authentication authentication) {
        UUID adminId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(disputeService.resolveDispute(id, adminId, resolutionNotes));
    }

    @PostMapping("/{id}/evidence")
    public ResponseEntity<DisputeEvidence> uploadEvidence(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String description,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(disputeService.uploadEvidence(id, userId, file, description));
    }

    @GetMapping("/{id}/evidence")
    public ResponseEntity<List<DisputeEvidence>> getEvidence(@PathVariable UUID id) {
        return ResponseEntity.ok(disputeService.getEvidence(id));
    }
}
