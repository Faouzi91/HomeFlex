package com.homeflex.features.property.api.v1;

import com.homeflex.features.property.dto.response.OccupancyResponse;
import com.homeflex.features.property.service.OccupancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties/{propertyId}/occupancy")
@RequiredArgsConstructor
public class OccupancyController {

    private final OccupancyService occupancyService;

    /**
     * Day-by-day occupancy view.
     * Standalone: AVAILABLE / BOOKED / BLOCKED per date.
     * Hotel: per-room-type booked/available counts per date.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('PROPERTY_READ')")
    public ResponseEntity<OccupancyResponse> getOccupancy(
            @PathVariable UUID propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(occupancyService.getOccupancy(propertyId, from, to));
    }

    /**
     * Aggregate occupancy rate + totals for a date range — used by the hosting dashboard card.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('PROPERTY_READ')")
    public ResponseEntity<OccupancyResponse.Summary> getSummary(
            @PathVariable UUID propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(occupancyService.getSummary(propertyId, from, to));
    }
}
