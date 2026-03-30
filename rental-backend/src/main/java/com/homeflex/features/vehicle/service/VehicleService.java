package com.homeflex.features.vehicle.service;

import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.core.service.KycService;
import com.homeflex.core.service.StorageService;
import com.homeflex.features.vehicle.domain.entity.ConditionReport;
import com.homeflex.features.vehicle.domain.entity.Vehicle;
import com.homeflex.features.vehicle.domain.entity.VehicleImage;
import com.homeflex.features.vehicle.domain.enums.VehicleStatus;
import com.homeflex.features.vehicle.domain.repository.ConditionReportRepository;
import com.homeflex.features.vehicle.domain.repository.VehicleImageRepository;
import com.homeflex.features.vehicle.domain.repository.VehicleRepository;
import com.homeflex.features.vehicle.dto.request.VehicleCreateRequest;
import com.homeflex.features.vehicle.dto.request.VehicleUpdateRequest;
import com.homeflex.features.vehicle.dto.response.ConditionReportResponse;
import com.homeflex.features.vehicle.dto.response.VehicleResponse;
import com.homeflex.features.vehicle.dto.response.VehicleSearchParams;
import com.homeflex.features.vehicle.mapper.VehicleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleImageRepository vehicleImageRepository;
    private final ConditionReportRepository conditionReportRepository;
    private final StorageService storageService;
    private final VehicleMapper vehicleMapper;
    private final KycService kycService;

    // ── Queries ─────────────────────────────────────────────────────────

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
        Vehicle vehicle = findActiveOrThrow(id);
        return vehicleMapper.toResponse(vehicle);
    }

    // ── Create ──────────────────────────────────────────────────────────

    @Transactional
    public VehicleResponse create(VehicleCreateRequest request, UUID ownerId) {
        kycService.requireVerified(ownerId);

        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setOwnerId(ownerId);
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setViewCount(0);

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle created: id={}, owner={}, brand={} model={}",
                saved.getId(), ownerId, saved.getBrand(), saved.getModel());
        return vehicleMapper.toResponse(saved);
    }

    // ── Update ──────────────────────────────────────────────────────────

    @Transactional
    public VehicleResponse update(UUID id, VehicleUpdateRequest request, UUID ownerId) {
        Vehicle vehicle = findActiveOrThrow(id);
        verifyOwnership(vehicle, ownerId);

        if (request.description() != null) vehicle.setDescription(request.description());
        if (request.dailyPrice() != null)   vehicle.setDailyPrice(request.dailyPrice());
        if (request.pickupCity() != null)    vehicle.setPickupCity(request.pickupCity());
        if (request.pickupAddress() != null) vehicle.setPickupAddress(request.pickupAddress());
        if (request.mileage() != null)       vehicle.setMileage(request.mileage());
        if (request.color() != null)         vehicle.setColor(request.color());

        vehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle updated: id={}, owner={}", id, ownerId);
        return vehicleMapper.toResponse(vehicle);
    }

    // ── Soft-delete ─────────────────────────────────────────────────────

    @Transactional
    public void softDelete(UUID id, UUID ownerId) {
        Vehicle vehicle = findActiveOrThrow(id);
        verifyOwnership(vehicle, ownerId);

        vehicle.setDeletedAt(LocalDateTime.now());
        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        vehicleRepository.save(vehicle);
        log.info("Vehicle soft-deleted: id={}, owner={}", id, ownerId);
    }

    // ── Image uploads ───────────────────────────────────────────────────

    @Transactional
    public VehicleResponse uploadImages(UUID id, List<MultipartFile> files, UUID ownerId) {
        Vehicle vehicle = findActiveOrThrow(id);
        verifyOwnership(vehicle, ownerId);

        int currentMax = vehicle.getImages().stream()
                .mapToInt(VehicleImage::getDisplayOrder)
                .max().orElse(-1);

        for (int i = 0; i < files.size(); i++) {
            String url = storageService.uploadFile(files.get(i), "vehicles/" + id);

            VehicleImage image = new VehicleImage();
            image.setVehicle(vehicle);
            image.setImageUrl(url);
            image.setDisplayOrder(currentMax + 1 + i);
            image.setPrimary(vehicle.getImages().isEmpty() && i == 0);
            vehicleImageRepository.save(image);
        }

        // Refresh to include new images in the response
        vehicle = vehicleRepository.findById(id).orElseThrow();
        return vehicleMapper.toResponse(vehicle);
    }

    // ── View count ──────────────────────────────────────────────────────

    @Transactional
    public void incrementViewCount(UUID id) {
        Vehicle vehicle = findActiveOrThrow(id);
        vehicle.setViewCount(vehicle.getViewCount() + 1);
        vehicleRepository.save(vehicle);
    }

    // ── Condition reports ───────────────────────────────────────────────

    @Transactional
    public ConditionReportResponse createConditionReport(UUID vehicleId,
                                                         UUID reporterId,
                                                         String notes,
                                                         Integer mileageAt,
                                                         String fuelLevel,
                                                         UUID bookingId,
                                                         List<MultipartFile> photos) {
        Vehicle vehicle = findActiveOrThrow(vehicleId);
        verifyOwnership(vehicle, reporterId);

        List<String> imageUrls = new ArrayList<>();
        if (photos != null) {
            for (MultipartFile photo : photos) {
                imageUrls.add(storageService.uploadFile(
                        photo, "vehicles/" + vehicleId + "/condition"));
            }
        }

        ConditionReport report = new ConditionReport();
        report.setVehicleId(vehicleId);
        report.setBookingId(bookingId);
        report.setReporterId(reporterId);
        report.setNotes(notes);
        report.setMileageAt(mileageAt);
        report.setFuelLevel(fuelLevel);
        report.setImageUrls(imageUrls);

        report = conditionReportRepository.save(report);
        log.info("Condition report created: id={}, vehicle={}", report.getId(), vehicleId);
        return vehicleMapper.toConditionReportResponse(report);
    }

    @Transactional(readOnly = true)
    public List<ConditionReportResponse> getConditionReports(UUID vehicleId) {
        return conditionReportRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId)
                .stream()
                .map(vehicleMapper::toConditionReportResponse)
                .toList();
    }

    // ── Private helpers ─────────────────────────────────────────────────

    private Vehicle findActiveOrThrow(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + id));
        if (vehicle.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Vehicle not found: " + id);
        }
        return vehicle;
    }

    private void verifyOwnership(Vehicle vehicle, UUID ownerId) {
        if (!vehicle.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedException("Not authorized to modify this vehicle");
        }
    }
}
