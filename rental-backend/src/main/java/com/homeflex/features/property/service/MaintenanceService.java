package com.homeflex.features.property.service;

import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.NotificationType;
import com.homeflex.core.domain.enums.UserRole;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.core.service.NotificationService;
import com.homeflex.core.service.StorageService;
import com.homeflex.features.property.domain.entity.MaintenanceRequest;
import com.homeflex.features.property.domain.entity.MaintenanceRequestImage;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.enums.BookingStatus;
import com.homeflex.features.property.domain.enums.MaintenanceStatus;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.MaintenanceRequestImageRepository;
import com.homeflex.features.property.domain.repository.MaintenanceRequestRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.features.property.dto.request.MaintenanceRequestCreateRequest;
import com.homeflex.features.property.dto.request.MaintenanceStatusUpdateRequest;
import com.homeflex.features.property.dto.response.MaintenanceRequestResponse;
import com.homeflex.features.property.mapper.MaintenanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final MaintenanceRequestImageRepository maintenanceRequestImageRepository;
    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final NotificationService notificationService;
    private final MaintenanceMapper maintenanceMapper;

    @Transactional
    public MaintenanceRequestResponse createRequest(UUID tenantId, MaintenanceRequestCreateRequest requestDto) {
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + tenantId));
        Property property = propertyRepository.findById(requestDto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found: " + requestDto.getPropertyId()));

        // Validate that the tenant has an active/approved/completed booking for the property
        boolean hasBooking = bookingRepository.existsByPropertyIdAndTenantIdAndStatusIn(
                property.getId(), tenantId, Arrays.asList(BookingStatus.APPROVED, BookingStatus.COMPLETED)
        );

        if (!hasBooking) {
            throw new DomainException("You can only create maintenance requests for properties you have booked.");
        }

        MaintenanceRequest request = new MaintenanceRequest();
        request.setTenant(tenant);
        request.setProperty(property);
        request.setTitle(requestDto.getTitle());
        request.setDescription(requestDto.getDescription());
        request.setCategory(requestDto.getCategory());
        request.setPriority(requestDto.getPriority());
        request.setStatus(MaintenanceStatus.REPORTED);

        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        // Notify landlord
        notificationService.sendMaintenanceRequestNotification(
                property.getLandlord().getId(),
                tenant.getFirstName() + " " + tenant.getLastName(),
                property.getTitle(),
                property.getId()
        );

        return maintenanceMapper.toResponse(saved);
    }

    @Transactional
    public void uploadImages(UUID requestId, UUID tenantId, List<MultipartFile> files) {
        MaintenanceRequest request = getRequestEntity(requestId);

        if (!request.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedException("Only the reporter can upload images.");
        }

        for (MultipartFile file : files) {
            String url = storageService.uploadFile(file, "maintenance/" + requestId);
            MaintenanceRequestImage image = new MaintenanceRequestImage();
            image.setMaintenanceRequest(request);
            image.setImageUrl(url);
            maintenanceRequestImageRepository.save(image);
        }
    }

    @Transactional
    public MaintenanceRequestResponse updateStatus(UUID requestId, UUID landlordId, MaintenanceStatusUpdateRequest updateDto) {
        MaintenanceRequest request = getRequestEntity(requestId);

        if (!request.getProperty().getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Only the property owner can update the status.");
        }

        MaintenanceStatus oldStatus = request.getStatus();
        request.setStatus(updateDto.getStatus());
        request.setResolutionNotes(updateDto.getResolutionNotes());

        if (updateDto.getStatus() == MaintenanceStatus.RESOLVED) {
            request.setResolvedAt(LocalDateTime.now());
        }

        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        // Notify tenant
        if (oldStatus != updateDto.getStatus()) {
            notificationService.sendMaintenanceStatusUpdateNotification(
                    request.getTenant().getId(),
                    request.getTitle(),
                    updateDto.getStatus().name(),
                    request.getProperty().getId()
            );
        }

        return maintenanceMapper.toResponse(saved);
    }

    public List<MaintenanceRequestResponse> getTenantRequests(UUID tenantId) {
        return maintenanceRequestRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(maintenanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<MaintenanceRequestResponse> getLandlordRequests(UUID landlordId) {
        return maintenanceRequestRepository.findByLandlordIdOrderByCreatedAtDesc(landlordId).stream()
                .map(maintenanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    public MaintenanceRequestResponse getRequest(UUID requestId, UUID userId) {
        MaintenanceRequest request = getRequestEntity(requestId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Access check
        if (!request.getTenant().getId().equals(userId) &&
                !request.getProperty().getLandlord().getId().equals(userId) &&
                user.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Unauthorized access to this maintenance request.");
        }

        return maintenanceMapper.toResponse(request);
    }

    private MaintenanceRequest getRequestEntity(UUID id) {
        return maintenanceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRequest not found: " + id));
    }
}
