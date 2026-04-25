package com.homeflex.features.property.service;

import com.homeflex.core.service.NotificationService;
import com.homeflex.core.service.PaymentService;
import com.homeflex.features.property.mapper.BookingMapper;
import com.homeflex.features.property.dto.response.BookingDto;
import com.homeflex.features.property.dto.response.PaymentInitiationResponse;
import com.homeflex.features.property.dto.request.BookingCreateRequest;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.BookingAuditLogRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.entity.BookingAuditLog;
import com.homeflex.features.property.domain.entity.RoomType;
import com.homeflex.features.property.domain.repository.RoomTypeRepository;
import com.homeflex.features.property.domain.enums.BookingStatus;
import com.homeflex.features.property.domain.enums.BookingType;
import com.homeflex.features.property.domain.BookingStateMachine;
import com.homeflex.core.domain.entity.User;
import com.stripe.model.PaymentIntent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
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

/**
 * Booking business logic — production-grade state-machine-driven workflow.
 *
 * Every status mutation goes through {@link BookingStateMachine#transition}
 * and is recorded in the {@code booking_audit_log} table.
 *
 * Ownership is enforced at the controller layer via @PreAuthorize.
 */
@Slf4j
@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingAuditLogRepository auditLogRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final NotificationService notificationService;
    private final PaymentService paymentService;
    private final PricingService pricingService;
    private final BookingMapper bookingMapper;
    private final PropertyAvailabilityService availabilityService;
    private final RoomInventoryService roomInventoryService;
    private final com.homeflex.features.finance.service.FinanceService financeService;
    private final RedissonClient redissonClient;

    private final Counter bookingsCreatedCounter;
    private final Counter paymentsSucceededCounter;

    /** Statuses that count as "occupying" dates for overlap checks. */
    private static final List<BookingStatus> ACTIVE_STATUSES = List.of(
            BookingStatus.DRAFT, BookingStatus.PAYMENT_PENDING,
            BookingStatus.PENDING_APPROVAL, BookingStatus.APPROVED,
            BookingStatus.ACTIVE, BookingStatus.COMPLETED,
            BookingStatus.PENDING_MODIFICATION
    );

    public BookingService(BookingRepository bookingRepository,
                          BookingAuditLogRepository auditLogRepository,
                          PropertyRepository propertyRepository,
                          UserRepository userRepository,
                          RoomTypeRepository roomTypeRepository,
                          NotificationService notificationService,
                          PaymentService paymentService,
                          PricingService pricingService,
                          BookingMapper bookingMapper,
                          PropertyAvailabilityService availabilityService,
                          RoomInventoryService roomInventoryService,
                          com.homeflex.features.finance.service.FinanceService financeService,
                          RedissonClient redissonClient,
                          MeterRegistry meterRegistry) {
        this.bookingRepository = bookingRepository;
        this.auditLogRepository = auditLogRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.notificationService = notificationService;
        this.paymentService = paymentService;
        this.pricingService = pricingService;
        this.bookingMapper = bookingMapper;
        this.availabilityService = availabilityService;
        this.roomInventoryService = roomInventoryService;
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

    // ── Create Draft ──────────────────────────────────────────────────────────

    public BookingDto createDraftBooking(BookingCreateRequest request, UUID tenantId) {
        // Idempotency check
        if (request.idempotencyKey() != null) {
            var existing = bookingRepository.findByIdempotencyKey(request.idempotencyKey());
            if (existing.isPresent()) {
                log.info("Idempotent draft return: key={}", request.idempotencyKey());
                return bookingMapper.toDto(existing.get());
            }
        }

        String lockKey = "lock:booking:property:" + request.propertyId();
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                try {
                    return executeCreateDraft(request, tenantId);
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

    private BookingDto executeCreateDraft(BookingCreateRequest request, UUID tenantId) {
        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        validateBookingDates(request);

        boolean isHotel = property.getPropertyType().isHotelType();
        RoomType roomType = null;

        if (isHotel) {
            if (request.roomTypeId() == null) {
                throw new DomainException("Room type is required for hotel bookings");
            }
            roomType = roomTypeRepository.findById(request.roomTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
            if (!roomType.getProperty().getId().equals(property.getId())) {
                throw new DomainException("Room type does not belong to this property");
            }
            // Hotel uses count-based inventory — no date overlap check needed here
        } else {
            validateNoDateOverlap(request);
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal platformFee = BigDecimal.ZERO;
        BigDecimal cleaningFee = property.getCleaningFee() != null ? property.getCleaningFee() : BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        int numberOfRooms = request.numberOfRooms();

        if (request.startDate() != null && request.endDate() != null) {
            BigDecimal basePrice;
            if (isHotel && roomType != null) {
                // Hotel: price = roomType.pricePerNight × nights × rooms
                long nights = java.time.temporal.ChronoUnit.DAYS.between(request.startDate(), request.endDate());
                if (nights < 1) nights = 1;
                basePrice = roomType.getPricePerNight()
                        .multiply(BigDecimal.valueOf(nights))
                        .multiply(BigDecimal.valueOf(numberOfRooms));
            } else {
                basePrice = pricingService.calculateBasePrice(property, request.startDate(), request.endDate());
            }
            platformFee = basePrice.multiply(new BigDecimal("0.15")).setScale(2, java.math.RoundingMode.HALF_UP);
            taxAmount   = basePrice.multiply(new BigDecimal("0.05")).setScale(2, java.math.RoundingMode.HALF_UP);
            totalPrice = basePrice.add(cleaningFee).add(taxAmount);
        }

        BookingType type = BookingType.valueOf(request.bookingType());

        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setTenant(tenant);
        booking.setRoomType(roomType);
        booking.setNumberOfRooms(numberOfRooms);
        booking.setBookingType(type);
        booking.setRequestedDate(request.requestedDate());
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setMessage(request.message());
        booking.setNumberOfOccupants(request.numberOfOccupants());
        booking.setStatus(BookingStatus.DRAFT);
        booking.setTotalPrice(totalPrice);
        booking.setPlatformFee(platformFee);
        booking.setCleaningFee(cleaningFee);
        booking.setTaxAmount(taxAmount);
        booking.setIdempotencyKey(request.idempotencyKey());

        booking = bookingRepository.save(booking);
        bookingsCreatedCounter.increment();

        audit(booking.getId(), null, BookingStatus.DRAFT, "CREATE_DRAFT", tenantId, null);

        // VIEWING bookings skip payment
        if (type == BookingType.VIEWING) {
            if (Boolean.TRUE.equals(property.getInstantBookEnabled())) {
                // Instant Book: auto-approve, no landlord action needed
                transitionAndSave(booking, BookingStatus.APPROVED, "INSTANT_BOOK_VIEWING", tenantId, null);
            } else {
                transitionAndSave(booking, BookingStatus.PENDING_APPROVAL, "SKIP_PAYMENT_VIEWING", tenantId, null);
                notificationService.sendBookingRequestNotification(property.getLandlord().getId(), tenant, property);
            }
        }

        return bookingMapper.toDto(booking);
    }

    // ── Initiate Payment ──────────────────────────────────────────────────────

    public PaymentInitiationResponse initiatePayment(UUID bookingId, UUID tenantId) {
        Booking booking = findBookingOrThrow(bookingId);

        if (booking.getBookingType() == BookingType.VIEWING) {
            throw new DomainException("VIEWING bookings do not require payment");
        }

        BookingStateMachine.transition(booking.getStatus(), BookingStatus.PAYMENT_PENDING);

        // Re-validate availability before charging
        if (booking.getStartDate() != null && booking.getEndDate() != null) {
            validateNoDateOverlapForBooking(booking);
        }

        Property property = booking.getProperty();
        String transferGroup = "property_booking_" + booking.getId();
        String description  = "HomeFlex booking: " + property.getTitle();

        PaymentIntent pi = paymentService.createBookingPaymentIntent(
                booking.getTotalPrice(), property.getCurrency(), description, transferGroup);

        booking.setStripePaymentIntentId(pi.getId());
        booking.setPaymentStatus("requires_payment_method");
        transitionAndSave(booking, BookingStatus.PAYMENT_PENDING, "INITIATE_PAYMENT", tenantId, null);

        return new PaymentInitiationResponse(
                booking.getId(), pi.getClientSecret(), pi.getId(),
                booking.getTotalPrice(), property.getCurrency());
    }

    // ── Retry Payment ─────────────────────────────────────────────────────────

    public PaymentInitiationResponse retryPayment(UUID bookingId, UUID tenantId) {
        Booking booking = findBookingOrThrow(bookingId);
        BookingStateMachine.transition(booking.getStatus(), BookingStatus.PAYMENT_PENDING);

        // Cancel old PI if exists
        if (booking.getStripePaymentIntentId() != null) {
            try { paymentService.cancelPaymentIntent(booking.getStripePaymentIntentId()); }
            catch (Exception e) { log.warn("Could not cancel old PI on retry: {}", e.getMessage()); }
        }

        Property property = booking.getProperty();
        String transferGroup = "property_booking_" + booking.getId();
        String description  = "HomeFlex booking (retry): " + property.getTitle();

        PaymentIntent pi = paymentService.createBookingPaymentIntent(
                booking.getTotalPrice(), property.getCurrency(), description, transferGroup);

        booking.setStripePaymentIntentId(pi.getId());
        booking.setPaymentStatus("requires_payment_method");
        booking.setPaymentFailureReason(null);
        transitionAndSave(booking, BookingStatus.PAYMENT_PENDING, "RETRY_PAYMENT", tenantId, null);

        return new PaymentInitiationResponse(
                booking.getId(), pi.getClientSecret(), pi.getId(),
                booking.getTotalPrice(), property.getCurrency());
    }

    // ── Confirm Payment (webhook) ─────────────────────────────────────────────

    public void confirmPayment(UUID bookingId) {
        Booking booking = findBookingOrThrow(bookingId);

        // Idempotent: if already past PAYMENT_PENDING, skip
        if (booking.getStatus() != BookingStatus.PAYMENT_PENDING) {
            log.info("confirmPayment no-op: booking {} already in {}", bookingId, booking.getStatus());
            return;
        }

        booking.setPaymentConfirmedAt(LocalDateTime.now());
        booking.setPaymentStatus("succeeded");
        paymentsSucceededCounter.increment();

        if (Boolean.TRUE.equals(booking.getProperty().getInstantBookEnabled())) {
            // Instant Book: auto-approve after payment, no landlord action needed
            transitionAndSave(booking, BookingStatus.APPROVED, "INSTANT_BOOK_CONFIRM_PAYMENT", null, null);
        } else {
            transitionAndSave(booking, BookingStatus.PENDING_APPROVAL, "CONFIRM_PAYMENT", null, null);
            notificationService.sendBookingRequestNotification(
                    booking.getProperty().getLandlord().getId(), booking.getTenant(), booking.getProperty());
        }

        try {
            financeService.generateReceipt(booking);
        } catch (Exception e) {
            log.error("Failed to generate receipt for booking {}: {}", booking.getId(), e.getMessage());
        }
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByTenant(UUID tenantId) {
        return bookingMapper.toDto(bookingRepository.findByTenantIdOrderByCreatedAtDesc(tenantId));
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByProperty(UUID propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            throw new ResourceNotFoundException("Property not found");
        }
        return bookingMapper.toDto(bookingRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId));
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(UUID bookingId) {
        return bookingMapper.toDto(findBookingOrThrow(bookingId));
    }

    @Transactional(readOnly = true)
    public List<BookingAuditLog> getAuditLog(UUID bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new ResourceNotFoundException("Booking not found");
        }
        return auditLogRepository.findByBookingIdOrderByCreatedAtAsc(bookingId);
    }

    // ── Landlord actions ─────────────────────────────────────────────────────

    public BookingDto approveBooking(UUID bookingId, String response) {
        Booking booking = findBookingOrThrow(bookingId);
        BookingStateMachine.transition(booking.getStatus(), BookingStatus.APPROVED);

        // For paid bookings with an active Stripe PI, ensure payment was confirmed before approving.
        // If there is no PI (e.g. sample/offline bookings), allow approval without payment check.
        if (requiresPayment(booking.getBookingType())
                && booking.getStripePaymentIntentId() != null
                && booking.getPaymentConfirmedAt() == null) {
            throw new DomainException("Cannot approve: Stripe payment has not been confirmed yet.");
        }

        // Capture the payment if using manual capture
        if (booking.getStripePaymentIntentId() != null) {
            try {
                paymentService.capturePaymentIntent(booking.getStripePaymentIntentId());
                if (booking.getPaymentConfirmedAt() == null) {
                    booking.setPaymentConfirmedAt(LocalDateTime.now());
                }
            } catch (Exception e) {
                log.warn("Could not capture payment on approve (will retry on webhook): {}", e.getMessage());
            }
        }

        booking.setLandlordResponse(response);
        booking.setRespondedAt(LocalDateTime.now());
        transitionAndSave(booking, BookingStatus.APPROVED, "APPROVE", null, response);

        // Reserve dates — hotel uses count-based inventory, standalone uses sparse calendar
        if (booking.getStartDate() != null && booking.getEndDate() != null) {
            if (booking.getRoomType() != null) {
                roomInventoryService.reserveRooms(
                        booking.getRoomType().getId(),
                        booking.getStartDate(), booking.getEndDate(), booking.getNumberOfRooms());
            } else {
                availabilityService.reserveForBooking(
                        booking.getProperty().getId(), booking.getId(),
                        booking.getStartDate(), booking.getEndDate());
            }
        }

        notificationService.sendBookingResponseNotification(
                booking.getTenant().getId(), booking.getProperty(), true);

        return bookingMapper.toDto(booking);
    }

    public BookingDto rejectBooking(UUID bookingId, String reason) {
        Booking booking = findBookingOrThrow(bookingId);
        BookingStateMachine.transition(booking.getStatus(), BookingStatus.REJECTED);

        if (booking.getStripePaymentIntentId() != null) {
            try {
                if (booking.getPaymentConfirmedAt() != null) {
                    paymentService.refundPayment(booking.getStripePaymentIntentId(), null,
                            booking.getProperty().getCurrency());
                } else {
                    paymentService.cancelPaymentIntent(booking.getStripePaymentIntentId());
                }
            } catch (Exception e) {
                log.warn("Payment cleanup failed on reject for booking {}: {}", bookingId, e.getMessage());
            }
        }

        booking.setLandlordResponse(reason);
        booking.setRespondedAt(LocalDateTime.now());
        transitionAndSave(booking, BookingStatus.REJECTED, "REJECT", null, reason);

        notificationService.sendBookingResponseNotification(
                booking.getTenant().getId(), booking.getProperty(), false);

        return bookingMapper.toDto(booking);
    }

    public BookingDto approveModification(UUID bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        BookingStateMachine.transition(booking.getStatus(), BookingStatus.APPROVED);

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
        transitionAndSave(booking, BookingStatus.APPROVED, "APPROVE_MODIFICATION", null, null);

        if (booking.getRoomType() != null) {
            roomInventoryService.reserveRooms(booking.getRoomType().getId(),
                    booking.getStartDate(), booking.getEndDate(), booking.getNumberOfRooms());
        } else {
            availabilityService.reserveForBooking(
                    booking.getProperty().getId(), booking.getId(),
                    booking.getStartDate(), booking.getEndDate());
        }

        return bookingMapper.toDto(booking);
    }

    public BookingDto rejectModification(UUID bookingId, String reason) {
        Booking booking = findBookingOrThrow(bookingId);
        BookingStateMachine.transition(booking.getStatus(), BookingStatus.APPROVED);

        booking.setLandlordResponse(reason);
        booking.setProposedStartDate(null);
        booking.setProposedEndDate(null);
        booking.setModificationReason(null);
        transitionAndSave(booking, BookingStatus.APPROVED, "REJECT_MODIFICATION", null, reason);

        return bookingMapper.toDto(booking);
    }

    // ── Tenant actions ────────────────────────────────────────────────────────

    public BookingDto cancelBooking(UUID bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        BookingStateMachine.transition(booking.getStatus(), BookingStatus.CANCELLED);

        // Handle payment cleanup based on current state
        if (booking.getStripePaymentIntentId() != null) {
            try {
                if (booking.getPaymentConfirmedAt() != null) {
                    paymentService.refundPayment(booking.getStripePaymentIntentId(), null,
                            booking.getProperty().getCurrency());
                } else {
                    paymentService.cancelPaymentIntent(booking.getStripePaymentIntentId());
                }
            } catch (Exception e) {
                log.warn("Payment cleanup failed on cancel for booking {}: {}", bookingId, e.getMessage());
            }
        }

        transitionAndSave(booking, BookingStatus.CANCELLED, "CANCEL", null, null);
        releaseInventory(booking);

        return bookingMapper.toDto(booking);
    }

    /**
     * Early checkout: completes an active booking and issues a prorated refund
     * for unused nights (today → original end date).
     */
    public BookingDto earlyCheckout(UUID bookingId) {
        Booking booking = findBookingOrThrow(bookingId);
        BookingStateMachine.transition(booking.getStatus(), BookingStatus.COMPLETED);

        if (booking.getStripePaymentIntentId() != null && booking.getPaymentConfirmedAt() != null
                && booking.getEndDate() != null) {
            LocalDate today = LocalDate.now();
            LocalDate end = booking.getEndDate();
            if (today.isBefore(end)) {
                long unusedNights = ChronoUnit.DAYS.between(today, end);
                long totalNights = ChronoUnit.DAYS.between(booking.getStartDate(), end);
                if (totalNights > 0 && booking.getTotalPrice() != null) {
                    BigDecimal pricePerNight = booking.getTotalPrice()
                            .divide(BigDecimal.valueOf(totalNights), 2, java.math.RoundingMode.HALF_UP);
                    BigDecimal refundAmount = pricePerNight.multiply(BigDecimal.valueOf(unusedNights));
                    try {
                        paymentService.refundPayment(booking.getStripePaymentIntentId(), refundAmount,
                                booking.getProperty().getCurrency());
                        log.info("Early checkout refund: booking={}, nights={}, amount={}",
                                bookingId, unusedNights, refundAmount);
                    } catch (Exception e) {
                        log.warn("Early checkout refund failed for booking {}: {}", bookingId, e.getMessage());
                    }
                }
            }
        }

        transitionAndSave(booking, BookingStatus.COMPLETED, "EARLY_CHECKOUT", null, null);
        releaseInventory(booking);

        return bookingMapper.toDto(booking);
    }

    public BookingDto requestModification(UUID bookingId, LocalDate newStart, LocalDate newEnd, String reason) {
        Booking booking = findBookingOrThrow(bookingId);
        BookingStateMachine.transition(booking.getStatus(), BookingStatus.PENDING_MODIFICATION);

        if (newEnd.isBefore(newStart)) throw new DomainException("Invalid dates");

        boolean overlap = bookingRepository.existsDateOverlapForPropertyExcludingBooking(
                booking.getProperty().getId(), booking.getId(), newStart, newEnd, ACTIVE_STATUSES);
        if (overlap) throw new ConflictException("Dates overlap");

        booking.setProposedStartDate(newStart);
        booking.setProposedEndDate(newEnd);
        booking.setModificationReason(reason);
        transitionAndSave(booking, BookingStatus.PENDING_MODIFICATION, "REQUEST_MODIFICATION", null, reason);

        return bookingMapper.toDto(booking);
    }

    // ── Webhook / system handlers ─────────────────────────────────────────────

    public void handlePaymentSucceeded(String paymentIntentId) {
        bookingRepository.findByStripePaymentIntentId(paymentIntentId)
                .ifPresent(booking -> confirmPayment(booking.getId()));
    }

    public void handlePaymentFailed(String paymentIntentId) {
        bookingRepository.findByStripePaymentIntentId(paymentIntentId)
                .ifPresent(booking -> {
                    if (booking.getStatus() == BookingStatus.PAYMENT_PENDING) {
                        booking.setPaymentStatus("failed");
                        booking.setPaymentFailureReason("Payment declined by provider");
                        transitionAndSave(booking, BookingStatus.PAYMENT_FAILED,
                                "PAYMENT_FAILED", null, "Payment declined");
                    }
                });
    }

    // ── Scheduled jobs ────────────────────────────────────────────────────────

    @Scheduled(cron = "0 0 * * * *")
    public void autoRejectExpiredPendingBookings() {
        LocalDateTime cutoff24h = LocalDateTime.now().minusHours(24);
        List<Booking> expired = bookingRepository.findByStatusAndCreatedAtBefore(
                BookingStatus.PENDING_APPROVAL, cutoff24h);
        for (Booking booking : expired) {
            booking.setLandlordResponse("Auto-rejected (24h timeout)");
            booking.setRespondedAt(LocalDateTime.now());
            transitionAndSave(booking, BookingStatus.REJECTED, "AUTO_REJECT_TIMEOUT", null, "24h timeout");
            notificationService.sendBookingResponseNotification(
                    booking.getTenant().getId(), booking.getProperty(), false);
        }

        // Also clean up stale drafts (>1h) and stale payment-pending (>30min)
        LocalDateTime cutoff1h = LocalDateTime.now().minusHours(1);
        for (Booking draft : bookingRepository.findByStatusAndCreatedAtBefore(BookingStatus.DRAFT, cutoff1h)) {
            transitionAndSave(draft, BookingStatus.CANCELLED, "AUTO_CANCEL_STALE_DRAFT", null, "Stale draft");
        }

        LocalDateTime cutoff30m = LocalDateTime.now().minusMinutes(30);
        for (Booking pp : bookingRepository.findByStatusAndCreatedAtBefore(BookingStatus.PAYMENT_PENDING, cutoff30m)) {
            if (pp.getStripePaymentIntentId() != null) {
                try { paymentService.cancelPaymentIntent(pp.getStripePaymentIntentId()); }
                catch (Exception e) { log.warn("Failed to cancel stale PI: {}", e.getMessage()); }
            }
            transitionAndSave(pp, BookingStatus.CANCELLED, "AUTO_CANCEL_STALE_PAYMENT", null, "Payment timeout");
        }
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void activateApprovedBookings() {
        List<Booking> ready = bookingRepository.findByStatusAndStartDateLessThanEqual(
                BookingStatus.APPROVED, LocalDate.now());
        for (Booking booking : ready) {
            transitionAndSave(booking, BookingStatus.ACTIVE, "AUTO_ACTIVATE", null, null);
            log.info("Booking {} activated (start date reached)", booking.getId());
        }
    }

    @Scheduled(cron = "0 0 12 * * *")
    public void completeActiveBookings() {
        List<Booking> finished = bookingRepository.findByStatusAndEndDateLessThan(
                BookingStatus.ACTIVE, LocalDate.now());
        for (Booking booking : finished) {
            transitionAndSave(booking, BookingStatus.COMPLETED, "AUTO_COMPLETE", null, null);
            log.info("Booking {} completed (end date passed)", booking.getId());
        }
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private Booking findBookingOrThrow(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    }

    private boolean requiresPayment(BookingType type) {
        return type == BookingType.RENTAL || type == BookingType.PURCHASE;
    }

    private void transitionAndSave(Booking booking, BookingStatus target, String action,
                                   UUID userId, String reason) {
        BookingStatus from = booking.getStatus();
        BookingStateMachine.transition(from, target);
        booking.setStatus(target);
        bookingRepository.save(booking);
        audit(booking.getId(), from, target, action, userId, reason);
        log.info("Booking {} transitioned: {} → {} (action={})", booking.getId(), from, target, action);
    }

    private void audit(UUID bookingId, BookingStatus from, BookingStatus to,
                       String action, UUID userId, String reason) {
        auditLogRepository.save(BookingAuditLog.of(bookingId, from, to, action, userId, reason));
    }

    private void validateBookingDates(BookingCreateRequest request) {
        if (request.startDate() != null && request.endDate() != null
                && request.endDate().isBefore(request.startDate())) {
            throw new DomainException("End date must be on or after start date");
        }
    }

    private void releaseInventory(Booking booking) {
        if (booking.getStartDate() == null || booking.getEndDate() == null) return;
        if (booking.getRoomType() != null) {
            roomInventoryService.releaseRooms(booking.getRoomType().getId(),
                    booking.getStartDate(), booking.getEndDate(), booking.getNumberOfRooms());
        } else {
            availabilityService.releaseForBooking(booking.getId());
        }
    }

    private void validateNoDateOverlap(BookingCreateRequest request) {
        if (request.startDate() == null || request.endDate() == null) return;
        boolean overlaps = bookingRepository.existsDateOverlapForProperty(
                request.propertyId(), request.startDate(), request.endDate(), ACTIVE_STATUSES);
        if (overlaps) throw new ConflictException("Selected dates overlap with an existing booking");
    }

    private void validateNoDateOverlapForBooking(Booking booking) {
        boolean overlaps = bookingRepository.existsDateOverlapForPropertyExcludingBooking(
                booking.getProperty().getId(), booking.getId(),
                booking.getStartDate(), booking.getEndDate(), ACTIVE_STATUSES);
        if (overlaps) throw new ConflictException("Selected dates are no longer available");
    }
}
