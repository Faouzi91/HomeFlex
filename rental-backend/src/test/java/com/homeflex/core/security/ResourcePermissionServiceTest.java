package com.homeflex.core.security;

import com.homeflex.core.domain.entity.User;
import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ResourcePermissionService ownership rules.
 *
 * These cover the logic that was previously spread across OwnershipVerifier
 * and HomeFlexPermissionEvaluator. Centralizing tests here means ownership
 * rules have a single authoritative test location.
 */
@ExtendWith(MockitoExtension.class)
class ResourcePermissionServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private PropertyRepository propertyRepository;

    private ResourcePermissionService service;

    private User landlord;
    private User tenant;
    private Property property;
    private Booking booking;

    @Mock private com.homeflex.features.vehicle.domain.repository.VehicleRepository vehicleRepository;
    @Mock private com.homeflex.features.vehicle.domain.repository.VehicleBookingRepository vehicleBookingRepository;

    @BeforeEach
    void setUp() {
        service = new ResourcePermissionService(bookingRepository, propertyRepository, vehicleRepository, vehicleBookingRepository);

        landlord = new User();
        landlord.setId(UUID.randomUUID());

        tenant = new User();
        tenant.setId(UUID.randomUUID());

        property = new Property();
        property.setId(UUID.randomUUID());
        property.setLandlord(landlord);

        booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setTenant(tenant);
        booking.setProperty(property);
    }

    // ── Object-based overload (already-loaded entity) ─────────────────────────

    @Test
    void bookingApprove_landlordAllowed() {
        assertThat(service.isAllowed(landlord.getId(), booking, Permissions.BOOKING_APPROVE)).isTrue();
    }

    @Test
    void bookingApprove_tenantDenied() {
        assertThat(service.isAllowed(tenant.getId(), booking, Permissions.BOOKING_APPROVE)).isFalse();
    }

    @Test
    void bookingApprove_strangerDenied() {
        assertThat(service.isAllowed(UUID.randomUUID(), booking, Permissions.BOOKING_APPROVE)).isFalse();
    }

    @Test
    void bookingCancel_tenantAllowed() {
        assertThat(service.isAllowed(tenant.getId(), booking, Permissions.BOOKING_CANCEL)).isTrue();
    }

    @Test
    void bookingCancel_landlordDenied() {
        assertThat(service.isAllowed(landlord.getId(), booking, Permissions.BOOKING_CANCEL)).isFalse();
    }

    @Test
    void bookingRead_tenantAllowed() {
        assertThat(service.isAllowed(tenant.getId(), booking, Permissions.BOOKING_READ)).isTrue();
    }

    @Test
    void bookingRead_landlordAllowed() {
        assertThat(service.isAllowed(landlord.getId(), booking, Permissions.BOOKING_READ)).isTrue();
    }

    @Test
    void bookingRead_strangerDenied() {
        assertThat(service.isAllowed(UUID.randomUUID(), booking, Permissions.BOOKING_READ)).isFalse();
    }

    @Test
    void bookingUpdate_landlordAllowed() {
        assertThat(service.isAllowed(landlord.getId(), booking, Permissions.BOOKING_UPDATE)).isTrue();
    }

    @Test
    void bookingUpdate_tenantAllowed() {
        assertThat(service.isAllowed(tenant.getId(), booking, Permissions.BOOKING_UPDATE)).isTrue();
    }

    // ── Property ownership ────────────────────────────────────────────────────

    @Test
    void propertyUpdate_landlordAllowed() {
        assertThat(service.isAllowed(landlord.getId(), property, Permissions.PROPERTY_UPDATE)).isTrue();
    }

    @Test
    void propertyUpdate_strangerDenied() {
        assertThat(service.isAllowed(UUID.randomUUID(), property, Permissions.PROPERTY_UPDATE)).isFalse();
    }

    // ── ID-based overload (loads entity from repo) ────────────────────────────

    @Test
    void idBased_bookingApprove_landlordAllowed() {
        when(bookingRepository.findByIdWithParties(booking.getId())).thenReturn(Optional.of(booking));

        assertThat(service.isAllowed(landlord.getId(), "Booking", booking.getId(), Permissions.BOOKING_APPROVE))
                .isTrue();
    }

    @Test
    void idBased_bookingApprove_tenantDenied() {
        when(bookingRepository.findByIdWithParties(booking.getId())).thenReturn(Optional.of(booking));

        assertThat(service.isAllowed(tenant.getId(), "Booking", booking.getId(), Permissions.BOOKING_APPROVE))
                .isFalse();
    }

    @Test
    void idBased_bookingNotFound_returnsFalse() {
        when(bookingRepository.findByIdWithParties(any())).thenReturn(Optional.empty());

        assertThat(service.isAllowed(landlord.getId(), "Booking", UUID.randomUUID(), Permissions.BOOKING_APPROVE))
                .isFalse();
    }

    @Test
    void idBased_propertyRead_landlordAllowed() {
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));

        assertThat(service.isAllowed(landlord.getId(), "Property", property.getId(), Permissions.BOOKING_READ))
                .isTrue();
    }

    @Test
    void idBased_unknownType_returnsFalse() {
        assertThat(service.isAllowed(landlord.getId(), "Vehicle", UUID.randomUUID(), Permissions.BOOKING_READ))
                .isFalse();
    }

    // ── Unknown domain object ─────────────────────────────────────────────────

    @Test
    void unknownDomainObject_returnsFalse() {
        assertThat(service.isAllowed(landlord.getId(), "not a domain object", Permissions.BOOKING_READ))
                .isFalse();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static <T> T any() {
        return org.mockito.ArgumentMatchers.any();
    }
}
