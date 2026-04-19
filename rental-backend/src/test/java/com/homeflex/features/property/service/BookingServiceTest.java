package com.homeflex.features.property.service;

import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.UserRole;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookingService business logic.
 *
 * Ownership checks are NOT tested here — they live in ResourcePermissionServiceTest.
 * BookingService is intentionally free of ownership logic; security is enforced
 * at the controller annotation layer by HomeFlexPermissionEvaluator.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationService notificationService;
    @Mock private PaymentService paymentService;
    @Mock private BookingMapper bookingMapper;
    @Mock private PropertyAvailabilityService propertyAvailabilityService;
    @Mock private com.homeflex.features.finance.service.FinanceService financeService;
    @Mock private RedissonClient redissonClient;
    @Mock private RLock rLock;

    private BookingService bookingService;

    private User landlord;
    private User tenant;
    private Property property;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() throws InterruptedException {
        bookingService = new BookingService(
                bookingRepository, propertyRepository, userRepository,
                notificationService, paymentService, bookingMapper,
                propertyAvailabilityService, financeService, redissonClient,
                new SimpleMeterRegistry()
        );

        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);

        landlord = new User();
        landlord.setId(UUID.randomUUID());
        landlord.setRole(UserRole.LANDLORD);

        tenant = new User();
        tenant.setId(UUID.randomUUID());
        tenant.setRole(UserRole.TENANT);

        property = new Property();
        property.setId(UUID.randomUUID());
        property.setLandlord(landlord);
        property.setPrice(BigDecimal.valueOf(100));
        property.setCurrency("XAF");

        booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setProperty(property);
        booking.setTenant(tenant);
        booking.setStatus(BookingStatus.PENDING);
        booking.setStripePaymentIntentId("pi_test");

        bookingDto = mock(BookingDto.class);
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @Test
    void createBooking_success() {
        BookingCreateRequest request = new BookingCreateRequest(
                property.getId(), "RENTAL", null,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
                "Msg", 1
        );

        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(userRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(bookingRepository.existsDateOverlapForProperty(any(), any(), any(), any())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        PaymentIntent pi = mock(PaymentIntent.class);
        when(pi.getId()).thenReturn("pi_new");
        when(paymentService.createBookingPaymentIntent(any(), any(), any(), any())).thenReturn(pi);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.createBooking(request, tenant.getId());

        assertThat(result).isNotNull();
        verify(bookingRepository, atLeastOnce()).save(any());
    }

    @Test
    void createBooking_propertyNotFound_throws() {
        BookingCreateRequest request = new BookingCreateRequest(
                UUID.randomUUID(), "RENTAL", null, null, null, null, null);
        when(propertyRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(request, tenant.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createBooking_dateOverlap_throwsConflict() {
        BookingCreateRequest request = new BookingCreateRequest(
                property.getId(), "RENTAL", null,
                LocalDate.now(), LocalDate.now(), null, null
        );
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(userRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));
        when(bookingRepository.existsDateOverlapForProperty(any(), any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.createBooking(request, tenant.getId()))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void createBooking_invalidDates_throwsDomain() {
        BookingCreateRequest request = new BookingCreateRequest(
                property.getId(), "RENTAL", null,
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(1), null, null
        );
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(userRepository.findById(tenant.getId())).thenReturn(Optional.of(tenant));

        assertThatThrownBy(() -> bookingService.createBooking(request, tenant.getId()))
                .isInstanceOf(DomainException.class);
    }

    // ── Landlord actions ──────────────────────────────────────────────────────

    @Test
    void approveBooking_success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        bookingService.approveBooking(booking.getId(), "OK");

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);
        verify(paymentService).confirmPaymentIntent("pi_test");
    }

    @Test
    void rejectBooking_success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        bookingService.rejectBooking(booking.getId(), "No");

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.REJECTED);
        verify(paymentService).cancelPaymentIntent("pi_test");
    }

    // ── Tenant actions ────────────────────────────────────────────────────────

    @Test
    void cancelBooking_success() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        bookingService.cancelBooking(booking.getId());

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(paymentService).cancelPaymentIntent("pi_test");
    }

    @Test
    void getBookingById_notFound_throws() {
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBookingById(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
