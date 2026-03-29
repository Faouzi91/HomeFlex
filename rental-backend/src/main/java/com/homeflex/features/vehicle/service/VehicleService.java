package com.homeflex.features.vehicle.service;

import com.homeflex.features.vehicle.domain.entity.Vehicle;
import com.homeflex.features.vehicle.domain.repository.VehicleRepository;
import com.homeflex.features.vehicle.domain.enums.VehicleStatus;
import com.homeflex.features.vehicle.dto.request.VehicleCreateRequest;
import com.homeflex.features.vehicle.dto.response.VehicleResponse;
import com.homeflex.features.vehicle.dto.response.VehicleSearchParams;
import com.homeflex.features.vehicle.mapper.VehicleMapper;
import com.homeflex.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Transactional(readOnly = true)
    public Page<VehicleResponse> search(VehicleSearchParams params, Pageable pageable) {
        return vehicleRepository.search(
                params.brand(),
                params.model(),
                params.city(),
                params.transmission(),
                params.fuelType(),
                params.status(),
                params.minPrice(),
                params.maxPrice(),
                pageable
        ).map(vehicleMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VehicleResponse getById(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + id));
        return vehicleMapper.toResponse(vehicle);
    }

    @Transactional
    public VehicleResponse create(VehicleCreateRequest request, UUID ownerId) {
        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setOwnerId(ownerId);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setViewCount(0);

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle created: id={}, owner={}, brand={} model={}",
                saved.getId(), ownerId, saved.getBrand(), saved.getModel());
        return vehicleMapper.toResponse(saved);
    }
}
