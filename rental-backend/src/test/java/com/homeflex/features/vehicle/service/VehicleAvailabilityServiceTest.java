package com.homeflex.features.vehicle.service;

import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.features.vehicle.domain.entity.Vehicle;
import com.homeflex.features.vehicle.domain.entity.VehicleBooking;
import com.homeflex.features.vehicle.domain.enums.VehicleBookingStatus;
import com.homeflex.features.vehicle.domain.enums.VehicleStatus;
import com.homeflex.features.vehicle.domain.repository.VehicleBookingRepository;
import com.homeflex.features.vehicle.domain.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleAvailabilityServiceTest {

    @Mock private VehicleRepository vehicleRepository;
    @Mock private VehicleBookingRepository bookingRepository;

    @InjectMocks
    private VehicleAvailabilityService service;

    private Vehicle vehicle;
    private UUID ownerId;
    private UUID tenantId;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        startDate = LocalDate.now().plusDays(1);
        endDate = LocalDate.now().plusDays(3);

        vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());
        vehicle.setOwnerId(ownerId);
        vehicle.setDailyPrice(BigDecimal.valueOf(5000));
        vehicle.setCurrency("XAF");
        vehicle.setStatus(VehicleStatus.AVAILABLE);
    }

    // ── isAvailable ───────────────────────────────────────────────────

    @Test
    void isAvailable_noOverlap_returnsTrue() {
        when(bookingRepository.existsDateOverlap(eq(vehicle.getId()), any(), any(), anyList()))
                .thenReturn(false);

        assertThat(service.isAvailable(vehicle.getId(), startDate, endDate)).isTrue();
    }

    @Test
    void isAvailable_hasOverlap_returnsFalse() {
        when(bookingRepository.existsDateOverlap(eq(vehicle.getId()), any(), any(), anyList()))
                .thenReturn(true);

        assertThat(service.isAvailable(vehicle.getId(), startDate, endDate)).isFalse();
    }

    // ── reserve ───────────────────────────────────────────────────────

    @Test
    void reserve_success_calculatesCorrectTotal() {
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(bookingRepository.existsDateOverlap(eq(vehicle.getId()), any(), any(), anyList()))
                .thenReturn(false);
        when(bookingRepository.save(any(VehicleBooking.class))).thenAnswer(i -> {
            VehicleBooking b = i.getArgument(0);
            b.setId(UUID.randomUUID());
            return b;
        });

        VehicleBooking result = service.reserve(
                vehicle.getId(), tenantId, startDate, endDate, "Test message");

        assertThat(result).isNotNull();
        assertThat(result.getVehicleId()).isEqualTo(vehicle.getId());
        assertThat(result.getTenantId()).isEqualTo(tenantId);
        assertThat(result.getStatus()).isEqualTo(VehicleBookingStatus.PENDING);
        assertThat(result.getMessage()).isEqualTo("Test message");
        // 3 days: day1, day2, day3 = 3 * 5000 = 15000
        assertThat(result.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(15000));
        verify(bookingRepository).save(any(VehicleBooking.class));
    }

    @Test
    void reserve_vehicleNotFound_throws() {
        when(vehicleRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reserve(UUID.randomUUID(), tenantId, startDate, endDate, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle not found");
    }

    @Test
    void reserve_vehicleDeleted_throws() {
        vehicle.setDeletedAt(java.time.LocalDateTime.now());
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> service.reserve(vehicle.getId(), tenantId, startDate, endDate, null))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void reserve_vehicleNotAvailableStatus_throws() {
        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> service.reserve(vehicle.getId(), tenantId, startDate, endDate, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("not available for booking");
    }

    @Test
    void reserve_ownerCannotBookOwnVehicle_throws() {
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> service.reserve(vehicle.getId(), ownerId, startDate, endDate, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Owners cannot book");
    }

    @Test
    void reserve_dateOverlap_throwsConflict() {
        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));
        when(bookingRepository.existsDateOverlap(eq(vehicle.getId()), any(), any(), anyList()))
                .thenReturn(true);

        assertThatThrownBy(() -> service.reserve(vehicle.getId(), tenantId, startDate, endDate, null))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("overlap");
    }

    @Test
    void reserve_endDateBeforeStartDate_throws() {
        assertThatThrownBy(() -> service.reserve(
                vehicle.getId(), tenantId, endDate, startDate, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("End date must be on or after");
    }

    @Test
    void reserve_startDateInPast_throws() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        assertThatThrownBy(() -> service.reserve(
                vehicle.getId(), tenantId, yesterday, endDate, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("past");
    }

    @Test
    void reserve_nullDates_throws() {
        assertThatThrownBy(() -> service.reserve(
                vehicle.getId(), tenantId, null, endDate, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("required");
    }

    // ── getActiveBookings ─────────────────────────────────────────────

    @Test
    void getActiveBookings_returnsList() {
        VehicleBooking b1 = new VehicleBooking();
        b1.setId(UUID.randomUUID());
        when(bookingRepository.findByVehicleIdAndStatusInOrderByStartDateAsc(eq(vehicle.getId()), anyList()))
                .thenReturn(List.of(b1));

        List<VehicleBooking> result = service.getActiveBookings(vehicle.getId());

        assertThat(result).hasSize(1);
    }

    // ── getTenantBookings ─────────────────────────────────────────────

    @Test
    void getTenantBookings_returnsList() {
        when(bookingRepository.findByTenantIdOrderByCreatedAtDesc(tenantId))
                .thenReturn(List.of());

        List<VehicleBooking> result = service.getTenantBookings(tenantId);

        assertThat(result).isEmpty();
    }
}
