package com.homeflex.features.vehicle.api.v1;

import com.homeflex.features.vehicle.domain.enums.FuelType;
import com.homeflex.features.vehicle.domain.enums.Transmission;
import com.homeflex.features.vehicle.domain.enums.VehicleStatus;
import com.homeflex.features.vehicle.dto.request.VehicleCreateRequest;
import com.homeflex.features.vehicle.dto.response.VehicleResponse;
import com.homeflex.features.vehicle.dto.response.VehicleSearchParams;
import com.homeflex.features.vehicle.service.VehicleService;
import com.homeflex.core.dto.common.ApiPageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleV1Controller {

    private final VehicleService vehicleService;

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

    @PostMapping
    @PreAuthorize("hasAnyRole('LANDLORD', 'ADMIN')")
    public ResponseEntity<VehicleResponse> create(
            @Valid @RequestBody VehicleCreateRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        UUID ownerId = UUID.fromString(principal.getUsername());
        VehicleResponse response = vehicleService.create(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
