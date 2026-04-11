package com.homeflex.features.property.service;

import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.service.NotificationService;
import com.homeflex.core.service.StorageService;
import com.homeflex.features.property.domain.entity.MaintenanceRequest;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.enums.MaintenanceCategory;
import com.homeflex.features.property.domain.enums.MaintenancePriority;
import com.homeflex.features.property.domain.enums.MaintenanceStatus;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.MaintenanceRequestImageRepository;
import com.homeflex.features.property.domain.repository.MaintenanceRequestRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.features.property.dto.request.MaintenanceRequestCreateRequest;
import com.homeflex.features.property.dto.request.MaintenanceStatusUpdateRequest;
import com.homeflex.features.property.dto.response.MaintenanceRequestResponse;
import com.homeflex.features.property.mapper.MaintenanceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceServiceTest {

    @Mock
    private MaintenanceRequestRepository maintenanceRequestRepository;
    @Mock
    private MaintenanceRequestImageRepository maintenanceRequestImageRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StorageService storageService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private MaintenanceMapper maintenanceMapper;

    @InjectMocks
    private MaintenanceService maintenanceService;

    private User tenant;
    private User landlord;
    private Property property;
    private UUID tenantId;
    private UUID propertyId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        propertyId = UUID.randomUUID();
        tenant = new User();
        tenant.setId(tenantId);
        tenant.setFirstName("Tenant");
        tenant.setLastName("User");

        landlord = new User();
        landlord.setId(UUID.randomUUID());

        property = new Property();
        property.setId(propertyId);
        property.setTitle("Test Property");
        property.setLandlord(landlord);
    }

    @Test
    void createRequest_Success() {
        MaintenanceRequestCreateRequest dto = new MaintenanceRequestCreateRequest();
        dto.setPropertyId(propertyId);
        dto.setTitle("Leaking Pipe");
        dto.setDescription("Water is leaking in the kitchen.");
        dto.setCategory(MaintenanceCategory.PLUMBING);
        dto.setPriority(MaintenancePriority.HIGH);

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(bookingRepository.existsByPropertyIdAndTenantIdAndStatusIn(any(), any(), any()))
                .thenReturn(true);
        when(maintenanceRequestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(maintenanceMapper.toResponse(any())).thenReturn(new MaintenanceRequestResponse());

        MaintenanceRequestResponse response = maintenanceService.createRequest(tenantId, dto);

        assertNotNull(response);
        verify(maintenanceRequestRepository).save(any());
        verify(notificationService).createNotification(any(), any(), any(), any(), any(), any());
    }

    @Test
    void createRequest_NoBooking_ThrowsException() {
        MaintenanceRequestCreateRequest dto = new MaintenanceRequestCreateRequest();
        dto.setPropertyId(propertyId);

        when(userRepository.findById(tenantId)).thenReturn(Optional.of(tenant));
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(property));
        when(bookingRepository.existsByPropertyIdAndTenantIdAndStatusIn(any(), any(), any()))
                .thenReturn(false);

        assertThrows(DomainException.class, () -> maintenanceService.createRequest(tenantId, dto));
        verify(maintenanceRequestRepository, never()).save(any());
    }

    @Test
    void updateStatus_Success() {
        UUID requestId = UUID.randomUUID();
        MaintenanceRequest request = new MaintenanceRequest();
        request.setId(requestId);
        request.setTenant(tenant);
        request.setProperty(property);
        request.setStatus(MaintenanceStatus.REPORTED);

        MaintenanceStatusUpdateRequest updateDto = new MaintenanceStatusUpdateRequest();
        updateDto.setStatus(MaintenanceStatus.IN_PROGRESS);
        updateDto.setResolutionNotes("Technician scheduled.");

        when(maintenanceRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(maintenanceRequestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(maintenanceMapper.toResponse(any())).thenReturn(new MaintenanceRequestResponse());

        MaintenanceRequestResponse response = maintenanceService.updateStatus(requestId, landlord.getId(), updateDto);

        assertNotNull(response);
        assertEquals(MaintenanceStatus.IN_PROGRESS, request.getStatus());
        verify(notificationService).createNotification(any(), any(), any(), any(), any(), any());
    }
}
