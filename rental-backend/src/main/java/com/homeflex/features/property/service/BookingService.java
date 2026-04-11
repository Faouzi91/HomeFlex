package com.homeflex.features.property.service;

import com.homeflex.core.service.NotificationService;
import com.homeflex.core.service.PaymentService;
import com.homeflex.features.property.mapper.BookingMapper;
import com.homeflex.features.property.dto.response.BookingDto;
import com.homeflex.features.property.dto.request.BookingCreateRequest;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.core.domain.entity.User;
import com.homeflex.features.property.domain.enums.BookingStatus;
import com.homeflex.features.property.domain.enums.BookingType;
import com.homeflex.core.domain.enums.UserRole;
import com.stripe.model.PaymentIntent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PaymentService paymentService;
    private final BookingMapper bookingMapper;
    private final PropertyAvailabilityService availabilityService;
    private final com.homeflex.features.finance.service.FinanceService financeService;

    private final Counter bookingsCreatedCounter;
    private final Counter paymentsSucceededCounter;
    private final Counter paymentsFailedCounter;

    public BookingService(BookingRepository bookingRepository,
                          PropertyRepository propertyRepository,
                          UserRepository userRepository,
                          NotificationService notificationService,
                          PaymentService paymentService,
                          BookingMapper bookingMapper,
                          PropertyAvailabilityService availabilityService,
                          com.homeflex.features.finance.service.FinanceService financeService,
                          MeterRegistry meterRegistry) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.paymentService = paymentService;
        this.bookingMapper = bookingMapper;
        this.availabilityService = availabilityService;
        this.financeService = financeService;

        this.bookingsCreatedCounter = Counter.builder("homeflex.bookings.created")
                .description("Total bookings created")
                .register(meterRegistry);
        this.paymentsSucceededCounter = Counter.builder("homeflex.bookings.payments")
                .tag("outcome", "success")
                .description("Successful booking payment intents")
                .register(meterRegistry);
        this.paymentsFailedCounter = Counter.builder("homeflex.bookings.payments")
                .tag("outcome", "failure")
                .description("Failed booking payment intents")
                .register(meterRegistry);
    }

    public BookingDto createBooking(BookingCreateRequest request, UUID tenantId) {
        // Get property
        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        // Get tenant
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        // Validate tenant role
        if (tenant.getRole() != UserRole.TENANT) {
            throw new UnauthorizedException("Only tenants can create bookings");
        }

        validateBookingDates(request);
        validateNoDateOverlap(request);

        // Compute total price for rental bookings
        BigDecimal totalPrice = null;
        BigDecimal platformFee = null;
        if (request.startDate() != null && request.endDate() != null && property.getPrice() != null) {
            long days = ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;
            totalPrice = property.getPrice().multiply(BigDecimal.valueOf(days));
            platformFee = paymentService.computePlatformFee(totalPrice);
        }

        // Create booking
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setTenant(tenant);
        booking.setBookingType(BookingType.valueOf(request.bookingType()));
        booking.setRequestedDate(request.requestedDate());
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setMessage(request.message());
        booking.setNumberOfOccupants(request.numberOfOccupants());
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(totalPrice);
        booking.setPlatformFee(platformFee);

        booking = bookingRepository.save(booking);
        bookingsCreatedCounter.increment();

        // Create PaymentIntent (funds held on platform account as escrow)
        if (totalPrice != null && totalPrice.compareTo(BigDecimal.ZERO) > 0) {
            String transferGroup = "property_booking_" + booking.getId();
            String description = "HomeFlex booking: " + property.getTitle();
            PaymentIntent pi = paymentService.createBookingPaymentIntent(
                    totalPrice, property.getCurrency(), description, transferGroup);
            booking.setStripePaymentIntentId(pi.getId());
            booking = bookingRepository.save(booking);
            paymentsSucceededCounter.increment();
            log.info("PaymentIntent created for booking {}: piId={}", booking.getId(), pi.getId());
        }

        // Notify landlord
        notificationService.sendBookingRequestNotification(
                property.getLandlord().getId(),
                tenant,
                property
        );

        return bookingMapper.toDto(booking);
    }

    private void validateBookingDates(BookingCreateRequest request) {
        if (request.startDate() != null
                && request.endDate() != null
                && request.endDate().isBefore(request.startDate())) {
            throw new DomainException("End date must be on or after start date");
        }
    }

    private void validateNoDateOverlap(BookingCreateRequest request) {
        if (request.startDate() == null || request.endDate() == null) {
            return;
        }

        boolean overlaps = bookingRepository.existsDateOverlapForProperty(
                request.propertyId(),
                request.startDate(),
                request.endDate(),
                Arrays.asList(BookingStatus.PENDING, BookingStatus.APPROVED, BookingStatus.COMPLETED)
        );

        if (overlaps) {
            throw new ConflictException("Selected dates overlap with an existing booking");
        }
    }

    public List<BookingDto> getBookingsByTenant(UUID tenantId) {
        return bookingMapper.toDto(bookingRepository.findByTenantIdOrderByCreatedAtDesc(tenantId));
    }

    public List<BookingDto> getBookingsByProperty(UUID propertyId, UUID landlordId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        // Verify ownership
        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to view these bookings");
        }

        return bookingMapper.toDto(bookingRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId));
    }

    public BookingDto getBookingById(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Verify user is either tenant or landlord
        if (!booking.getTenant().getId().equals(userId) &&
                !booking.getProperty().getLandlord().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to view this booking");
        }

        return bookingMapper.toDto(booking);
    }

    public BookingDto approveBooking(UUID bookingId, UUID landlordId, String response) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Verify landlord owns the property
        if (!booking.getProperty().getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to approve this booking");
        }

        // Confirm the PaymentIntent to charge the tenant
        if (booking.getStripePaymentIntentId() != null) {
            paymentService.confirmPaymentIntent(booking.getStripePaymentIntentId());
            log.info("Payment confirmed for booking {}: piId={}", bookingId, booking.getStripePaymentIntentId());
        }

        booking.setStatus(BookingStatus.APPROVED);
        booking.setLandlordResponse(response);
        booking.setRespondedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);

        // Reserve the calendar dates. The unique constraint on
        // (property_id, date) is what guarantees no double-booking even under
        // concurrent approvals.
        if (booking.getStartDate() != null && booking.getEndDate() != null) {
            availabilityService.reserveForBooking(
                    booking.getProperty().getId(),
                    booking.getId(),
                    booking.getStartDate(),
                    booking.getEndDate());
        }

        // Notify tenant
        notificationService.sendBookingResponseNotification(
                booking.getTenant().getId(),
                booking.getProperty(),
                true
        );

        return bookingMapper.toDto(booking);
    }

    public BookingDto rejectBooking(UUID bookingId, UUID landlordId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Verify landlord owns the property
        if (!booking.getProperty().getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to reject this booking");
        }

        // Cancel the PaymentIntent to release the hold
        if (booking.getStripePaymentIntentId() != null) {
            paymentService.cancelPaymentIntent(booking.getStripePaymentIntentId());
            log.info("Payment cancelled for rejected booking {}", bookingId);
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setLandlordResponse(reason);
        booking.setRespondedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);

        // Notify tenant
        notificationService.sendBookingResponseNotification(
                booking.getTenant().getId(),
                booking.getProperty(),
                false
        );

        return bookingMapper.toDto(booking);
    }

    /**
     * Called by the Stripe webhook when a PaymentIntent succeeds.
     * Sets paymentConfirmedAt on the matching booking.
     */
    public void handlePaymentSucceeded(String paymentIntentId) {
        bookingRepository.findByStripePaymentIntentId(paymentIntentId)
                .ifPresent(booking -> {
                    if (booking.getStatus() == BookingStatus.PENDING ||
                        booking.getStatus() == BookingStatus.APPROVED) {
                        booking.setPaymentConfirmedAt(LocalDateTime.now());
                        bookingRepository.save(booking);
                        log.info("Booking {} payment confirmed via webhook", booking.getId());
                        
                        // Generate automated receipt
                        try {
                            financeService.generateReceipt(booking);
                        } catch (Exception e) {
                            log.error("Failed to generate receipt for booking {}: {}", booking.getId(), e.getMessage());
                        }
                    }
                });
    }

    /**
     * Called by the Stripe webhook when a PaymentIntent fails.
     * Cancels the matching booking.
     */
    public void handlePaymentFailed(String paymentIntentId) {
        bookingRepository.findByStripePaymentIntentId(paymentIntentId)
                .ifPresent(booking -> {
                    booking.setStatus(BookingStatus.CANCELLED);
                    bookingRepository.save(booking);
                    availabilityService.releaseForBooking(booking.getId());
                    log.warn("Booking {} cancelled due to payment failure", booking.getId());
                });
    }

    public BookingDto cancelBooking(UUID bookingId, UUID tenantId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Verify tenant owns the booking
        if (!booking.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedException("Not authorized to cancel this booking");
        }

        // Cancel the PaymentIntent if it exists
        if (booking.getStripePaymentIntentId() != null) {
            paymentService.cancelPaymentIntent(booking.getStripePaymentIntentId());
            log.info("Payment cancelled for cancelled booking {}", bookingId);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);

        // Free the dates so they can be booked again.
        availabilityService.releaseForBooking(booking.getId());

        return bookingMapper.toDto(booking);
    }
}