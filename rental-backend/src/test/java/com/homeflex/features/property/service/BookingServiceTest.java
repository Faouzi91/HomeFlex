package com.homeflex.features.property.service;

import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.UserRole;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.core.service.NotificationService;
import com.homeflex.core.service.PaymentService;
import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.enums.BookingStatus;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.features.property.dto.request.BookingCreateRequest;
import com.homeflex.features.property.dto.response.BookingDto;
import com.homeflex.features.property.mapper.BookingMapper;
import com.stripe.model.PaymentIntent;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @Mock private PaymentService paymentService;
    @Mock private BookingMapper bookingMapper;

    private BookingService bookingService;

    private User landlord;
    private User tenant;
    private Property property;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService(
                bookingRepository, propertyRepository, userRepository,
                notificationService, paymentService, bookingMapper, new SimpleMeterRegistry()
        );

        landlord = new User();
        landlord.setId(UUID.randomUUID());
        landlord.setEmail("landlord@example.com");
        landlord.setRole(UserRole.LANDLORD);
        landlord.setStripeAccountId("acct_landlord");

        tenant = new User();
        tenant.setId(UUID.randomUUID());
        tenant.setEmail("tenant@example.com");
        tenant.setRole(UserRole.TENANT);

        property = new Property();
        property.setId(UUID.randomUUID());
        property.setTitle("Test Property");
        property.setLandlord(landlord);
        property.setPrice(BigDecimal.valueOf(50000));
        property.setCurrency("XAF");

        booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setProperty(property);
        booking.setTenant(tenant);
        booking.setStatus(BookingStatus.PENDING);
        booking.setStripePaymentIntentId("pi_test123");

        bookingDto = mock(BookingDto.class);
    }

    // ── Create Booking ─────────────────────────────────────────────────

    @Test
    void createBooking_success_withPayment() {
        BookingCreateRequest request = new BookingCreateRequest(
                property.getId(), "RENTAL", null,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(5),
                "Test message", 2
        );

        PaymentIntent pi = mock(PaymentIntent.class);
        when(pi.getId()).thenReturn("pi_test");

        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(userRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(bookingRepository.existsDateOverlapForProperty(any(), any(), any(), anyList())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> {
            Booking b = i.getArgument(0);
            if (b.getId() == null) b.setId(UUID.randomUUID());
            return b;
        });
        when(paymentService.computePlatformFee(any())).thenReturn(BigDecimal.valueOf(25000));
        when(paymentService.createBookingPaymentIntent(any(), anyString(), anyString(), anyString()))
                .thenReturn(pi);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.createBooking(request, tenant.getId());

        assertThat(result).isNotNull();
        verify(paymentService).createBookingPaymentIntent(any(), eq("XAF"), anyString(), anyString());
        verify(notificationService).sendBookingRequestNotification(eq(landlord.getId()), eq(tenant), eq(property));
    }

    @Test
    void createBooking_propertyNotFound_throws() {
        BookingCreateRequest request = new BookingCreateRequest(
                UUID.randomUUID(), "RENTAL", null, null, null, null, null
        );
        when(propertyRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(request, tenant.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createBooking_notTenant_throwsUnauthorized() {
        landlord.setRole(UserRole.LANDLORD);
        BookingCreateRequest request = new BookingCreateRequest(
                property.getId(), "RENTAL", null, null, null, null, null
        );
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(userRepository.findById(landlord.getId())).thenReturn(Optional.of(landlord));

        assertThatThrownBy(() -> bookingService.createBooking(request, landlord.getId()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only tenants");
    }

    @Test
    void createBooking_dateOverlap_throwsConflict() {
        BookingCreateRequest request = new BookingCreateRequest(
                property.getId(), "RENTAL", null,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(5),
                null, null
        );
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(userRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(bookingRepository.existsDateOverlapForProperty(any(), any(), any(), anyList())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(request, tenant.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("overlap");
    }

    @Test
    void createBooking_endDateBeforeStart_throws() {
        BookingCreateRequest request = new BookingCreateRequest(
                property.getId(), "RENTAL", null,
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(1),
                null, null
        );
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(userRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));

        assertThatThrownBy(() -> bookingService.createBooking(request, tenant.getId()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("End date");
    }

    // ── Approve Booking ────────────────────────────────────────────────

    @Test
    void approveBooking_success_confirmsPayment() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        bookingService.approveBooking(booking.getId(), landlord.getId(), "Approved!");

        verify(paymentService).confirmPaymentIntent("pi_test123");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
        verify(notificationService).sendBookingResponseNotification(eq(tenant.getId()), eq(property), eq(true));
    }

    @Test
    void approveBooking_wrongLandlord_throwsUnauthorized() {
        UUID otherLandlordId = UUID.randomUUID();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approveBooking(booking.getId(), otherLandlordId, null))
                .isInstanceOf(UnauthorizedException.class);
    }

    // ── Reject Booking ─────────────────────────────────────────────────

    @Test
    void rejectBooking_success_cancelsPayment() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        bookingService.rejectBooking(booking.getId(), landlord.getId(), "Not available");

        verify(paymentService).cancelPaymentIntent("pi_test123");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    // ── Cancel Booking ─────────────────────────────────────────────────

    @Test
    void cancelBooking_success_cancelsPayment() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        bookingService.cancelBooking(booking.getId(), tenant.getId());

        verify(paymentService).cancelPaymentIntent("pi_test123");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void cancelBooking_wrongTenant_throwsUnauthorized() {
        UUID otherTenantId = UUID.randomUUID();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancelBooking(booking.getId(), otherTenantId))
                .isInstanceOf(UnauthorizedException.class);
    }

    // ── Get Booking ────────────────────────────────────────────────────

    @Test
    void getBookingById_asTenant_success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getBookingById(booking.getId(), tenant.getId());
        assertThat(result).isNotNull();
    }

    @Test
    void getBookingById_asLandlord_success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getBookingById(booking.getId(), landlord.getId());
        assertThat(result).isNotNull();
    }

    @Test
    void getBookingById_unauthorized_throws() {
        UUID randomId = UUID.randomUUID();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(booking.getId(), randomId))
                .isInstanceOf(UnauthorizedException.class);
    }
}
