package com.homeflex.features.property.service;

import com.homeflex.core.service.NotificationService;
import com.homeflex.core.service.PaymentService;
import com.homeflex.features.property.mapper.BookingMapper;
import com.homeflex.features.property.dto.response.BookingDto;
import com.homeflex.features.property.dto.request.BookingCreateRequest;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.enums.BookingStatus;
import com.homeflex.features.property.domain.enums.BookingType;
import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.UserRole;
import com.stripe.model.PaymentIntent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private final RedissonClient redissonClient;

    private final Counter bookingsCreatedCounter;
    private final Counter paymentsSucceededCounter;

    public BookingService(BookingRepository bookingRepository,
                          PropertyRepository propertyRepository,
                          UserRepository userRepository,
                          NotificationService notificationService,
                          PaymentService paymentService,
                          BookingMapper bookingMapper,
                          PropertyAvailabilityService availabilityService,
                          com.homeflex.features.finance.service.FinanceService financeService,
                          RedissonClient redissonClient,
                          MeterRegistry meterRegistry) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.paymentService = paymentService;
        this.bookingMapper = bookingMapper;
        this.availabilityService = availabilityService;
        this.financeService = financeService;
        this.redissonClient = redissonClient;

        this.bookingsCreatedCounter = Counter.builder("homeflex.bookings.created")
                .description("Total bookings created")
                .register(meterRegistry);

        this.paymentsSucceededCounter = Counter.builder("homeflex.bookings.payments")
                .description("Successful booking payments")
                .tag("outcome", "success")
                .register(meterRegistry);
    }

    public BookingDto createBooking(BookingCreateRequest request, UUID tenantId) {
        String lockKey = "lock:booking:property:" + request.propertyId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                try {
                    return executeCreateBooking(request, tenantId);
                } finally {
                    lock.unlock();
                }
            } else {
                throw new DomainException("Could not acquire booking lock. Please try again.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DomainException("Booking interrupted");
        }
    }

    private BookingDto executeCreateBooking(BookingCreateRequest request, UUID tenantId) {
        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        if (tenant.getRole() != UserRole.TENANT) {
            throw new UnauthorizedException("Only tenants can create bookings");
        }

        validateBookingDates(request);
        validateNoDateOverlap(request);

        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal platformFee = BigDecimal.ZERO;
        BigDecimal cleaningFee = property.getCleaningFee() != null ? property.getCleaningFee() : BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        if (request.startDate() != null && request.endDate() != null && property.getPrice() != null) {
            long days = ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;
            BigDecimal basePrice = property.getPrice().multiply(BigDecimal.valueOf(days));
            
            platformFee = basePrice.multiply(new BigDecimal("0.15")).setScale(2, java.math.RoundingMode.HALF_UP);
            taxAmount = basePrice.multiply(new BigDecimal("0.05")).setScale(2, java.math.RoundingMode.HALF_UP);
            
            totalPrice = basePrice.add(cleaningFee).add(taxAmount);
        }

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
        booking.setCleaningFee(cleaningFee);
        booking.setTaxAmount(taxAmount);

        booking = bookingRepository.save(booking);
        bookingsCreatedCounter.increment();

        String stripeClientSecret = null;
        if (totalPrice != null && totalPrice.compareTo(BigDecimal.ZERO) > 0) {
            String transferGroup = "property_booking_" + booking.getId();
            String description = "HomeFlex booking: " + property.getTitle();
            PaymentIntent pi = paymentService.createBookingPaymentIntent(
                    totalPrice, property.getCurrency(), description, transferGroup);
            booking.setStripePaymentIntentId(pi.getId());
            stripeClientSecret = pi.getClientSecret();
            booking = bookingRepository.save(booking);
            paymentsSucceededCounter.increment();
        }

        notificationService.sendBookingRequestNotification(property.getLandlord().getId(), tenant, property);

        booking.setStripeClientSecret(stripeClientSecret);
        return bookingMapper.toDto(booking);
    }

    private void validateBookingDates(BookingCreateRequest request) {
        if (request.startDate() != null && request.endDate() != null && request.endDate().isBefore(request.startDate())) {
            throw new DomainException("End date must be on or after start date");
        }
    }

    private void validateNoDateOverlap(BookingCreateRequest request) {
        if (request.startDate() == null || request.endDate() == null) return;

        boolean overlaps = bookingRepository.existsDateOverlapForProperty(
                request.propertyId(), request.startDate(), request.endDate(),
                Arrays.asList(BookingStatus.PENDING, BookingStatus.APPROVED, BookingStatus.COMPLETED));

        if (overlaps) throw new ConflictException("Selected dates overlap with an existing booking");
    }

    public List<BookingDto> getBookingsByTenant(UUID tenantId) {
        return bookingMapper.toDto(bookingRepository.findByTenantIdOrderByCreatedAtDesc(tenantId));
    }

    public List<BookingDto> getBookingsByProperty(UUID propertyId, UUID landlordId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to view these bookings");
        }

        return bookingMapper.toDto(bookingRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId));
    }

    public BookingDto getBookingById(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getTenant().getId().equals(userId) &&
                !booking.getProperty().getLandlord().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to view this booking");
        }

        return bookingMapper.toDto(booking);
    }

    public BookingDto approveBooking(UUID bookingId, UUID landlordId, String response) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getProperty().getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to approve this booking");
        }

        if (booking.getStripePaymentIntentId() != null) {
            paymentService.confirmPaymentIntent(booking.getStripePaymentIntentId());
        }

        booking.setStatus(BookingStatus.APPROVED);
        booking.setLandlordResponse(response);
        booking.setRespondedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);

        if (booking.getStartDate() != null && booking.getEndDate() != null) {
            availabilityService.reserveForBooking(booking.getProperty().getId(), booking.getId(), booking.getStartDate(), booking.getEndDate());
        }

        notificationService.sendBookingResponseNotification(booking.getTenant().getId(), booking.getProperty(), true);

        return bookingMapper.toDto(booking);
    }

    public BookingDto rejectBooking(UUID bookingId, UUID landlordId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getProperty().getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to reject this booking");
        }

        if (booking.getStripePaymentIntentId() != null) {
            paymentService.cancelPaymentIntent(booking.getStripePaymentIntentId());
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setLandlordResponse(reason);
        booking.setRespondedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);

        notificationService.sendBookingResponseNotification(booking.getTenant().getId(), booking.getProperty(), false);

        return bookingMapper.toDto(booking);
    }

    public void handlePaymentSucceeded(String paymentIntentId) {
        bookingRepository.findByStripePaymentIntentId(paymentIntentId)
                .ifPresent(booking -> {
                    if (booking.getStatus() == BookingStatus.PENDING || booking.getStatus() == BookingStatus.APPROVED) {
                        booking.setPaymentConfirmedAt(LocalDateTime.now());
                        bookingRepository.save(booking);
                        try {
                            financeService.generateReceipt(booking);
                        } catch (Exception e) {
                            log.error("Failed to generate receipt for booking {}: {}", booking.getId(), e.getMessage());
                        }
                    }
                });
    }

    public void handlePaymentFailed(String paymentIntentId) {
        bookingRepository.findByStripePaymentIntentId(paymentIntentId)
                .ifPresent(booking -> {
                    booking.setStatus(BookingStatus.CANCELLED);
                    bookingRepository.save(booking);
                    availabilityService.releaseForBooking(booking.getId());
                });
    }

    public BookingDto cancelBooking(UUID bookingId, UUID tenantId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedException("Not authorized to cancel this booking");
        }

        if (booking.getStripePaymentIntentId() != null) {
            paymentService.cancelPaymentIntent(booking.getStripePaymentIntentId());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);
        availabilityService.releaseForBooking(booking.getId());

        return bookingMapper.toDto(booking);
    }

    public BookingDto requestModification(UUID bookingId, LocalDate newStart, LocalDate newEnd, String reason, UUID tenantId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getTenant().getId().equals(tenantId)) throw new UnauthorizedException("Not authorized");
        if (booking.getStatus() != BookingStatus.APPROVED) throw new DomainException("Only approved bookings can be modified");
        if (newEnd.isBefore(newStart)) throw new DomainException("Invalid dates");

        boolean overlap = bookingRepository.existsDateOverlapForPropertyExcludingBooking(
                booking.getProperty().getId(), booking.getId(), newStart, newEnd,
                Arrays.asList(BookingStatus.PENDING, BookingStatus.APPROVED, BookingStatus.COMPLETED, BookingStatus.PENDING_MODIFICATION));

        if (overlap) throw new ConflictException("Dates overlap");

        booking.setStatus(BookingStatus.PENDING_MODIFICATION);
        booking.setProposedStartDate(newStart);
        booking.setProposedEndDate(newEnd);
        booking.setModificationReason(reason);

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    public BookingDto approveModification(UUID bookingId, UUID landlordId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getProperty().getLandlord().getId().equals(landlordId)) throw new UnauthorizedException("Not authorized");
        if (booking.getStatus() != BookingStatus.PENDING_MODIFICATION) throw new DomainException("No pending modification");

        availabilityService.releaseForBooking(booking.getId());
        booking.setStartDate(booking.getProposedStartDate());
        booking.setEndDate(booking.getProposedEndDate());
        
        long days = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate()) + 1;
        BigDecimal newTotalPrice = booking.getProperty().getPrice().multiply(BigDecimal.valueOf(days));
        booking.setTotalPrice(newTotalPrice);
        booking.setPlatformFee(paymentService.computePlatformFee(newTotalPrice));

        booking.setProposedStartDate(null);
        booking.setProposedEndDate(null);
        booking.setModificationReason(null);
        booking.setStatus(BookingStatus.APPROVED);

        booking = bookingRepository.save(booking);
        availabilityService.reserveForBooking(booking.getProperty().getId(), booking.getId(), booking.getStartDate(), booking.getEndDate());

        return bookingMapper.toDto(booking);
    }

    public BookingDto rejectModification(UUID bookingId, String reason, UUID landlordId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getProperty().getLandlord().getId().equals(landlordId)) throw new UnauthorizedException("Not authorized");
        if (booking.getStatus() != BookingStatus.PENDING_MODIFICATION) throw new DomainException("No pending modification");

        booking.setStatus(BookingStatus.APPROVED);
        booking.setLandlordResponse(reason);
        booking.setProposedStartDate(null);
        booking.setProposedEndDate(null);
        booking.setModificationReason(null);

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 * * * *")
    public void autoRejectExpiredPendingBookings() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<Booking> expiredBookings = bookingRepository.findByStatusAndCreatedAtBefore(BookingStatus.PENDING, cutoff);

        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.REJECTED);
            booking.setLandlordResponse("Auto-rejected (24h timeout)");
            booking.setRespondedAt(LocalDateTime.now());
            bookingRepository.save(booking);
            notificationService.sendBookingResponseNotification(booking.getTenant().getId(), booking.getProperty(), false);
        }
    }
}
