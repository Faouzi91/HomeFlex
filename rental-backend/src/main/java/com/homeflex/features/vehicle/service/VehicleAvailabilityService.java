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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Manages vehicle availability and prevents double-booking for daily rentals.
 * <p>
 * Uses the same interval-overlap query strategy as the property
 * {@code BookingService} but is optimised for whole-day granularity:
 * {@code existing.start <= requested.end AND existing.end >= requested.start}.
 * Only bookings in {@code PENDING} or {@code CONFIRMED} state block
 * the calendar — cancelled and completed bookings are ignored.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleAvailabilityService {

    private static final List<VehicleBookingStatus> BLOCKING_STATUSES =
            List.of(VehicleBookingStatus.PAYMENT_PENDING, VehicleBookingStatus.PENDING_APPROVAL, VehicleBookingStatus.APPROVED, VehicleBookingStatus.ACTIVE);

    private final VehicleRepository vehicleRepository;
    private final VehicleBookingRepository bookingRepository;
    private final com.homeflex.core.service.PaymentService paymentService;

    @Transactional
    public void handlePaymentSucceeded(String paymentIntentId) {
        log.info("Handling payment success for vehicle booking: PI={}", paymentIntentId);
        VehicleBooking booking = bookingRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle booking not found for PI: " + paymentIntentId));

        if (booking.getStatus() == VehicleBookingStatus.PAYMENT_PENDING) {
            booking.setStatus(VehicleBookingStatus.PENDING_APPROVAL);
            booking.setPaymentStatus("succeeded");
            bookingRepository.save(booking);
            log.info("Vehicle booking {} moved to PENDING_APPROVAL", booking.getId());
        }
    }

    @Transactional
    public void handlePaymentFailed(String paymentIntentId) {
        log.warn("Handling payment failure for vehicle booking: PI={}", paymentIntentId);
        VehicleBooking booking = bookingRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle booking not found for PI: " + paymentIntentId));

        booking.setStatus(VehicleBookingStatus.PAYMENT_FAILED);
        booking.setPaymentStatus("failed");
        bookingRepository.save(booking);
    }

    @Transactional
    public void approve(UUID bookingId, UUID ownerId) {
        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle booking not found: " + bookingId));

        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!vehicle.getOwnerId().equals(ownerId)) {
            throw new DomainException("Only the vehicle owner can approve bookings");
        }

        if (booking.getStatus() != VehicleBookingStatus.PENDING_APPROVAL) {
            throw new DomainException("Booking is not in PENDING_APPROVAL status (current: " + booking.getStatus() + ")");
        }

        // Capture Stripe payment
        if (booking.getStripePaymentIntentId() != null) {
            try {
                paymentService.capturePaymentIntent(booking.getStripePaymentIntentId());
            } catch (Exception e) {
                log.warn("Could not capture payment for vehicle booking {}: {}", booking.getId(), e.getMessage());
            }
        }

        booking.setStatus(VehicleBookingStatus.APPROVED);
        bookingRepository.save(booking);
        log.info("Vehicle booking {} approved by owner {}", bookingId, ownerId);
    }

    @Transactional
    public void reject(UUID bookingId, UUID ownerId, String reason) {
        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle booking not found: " + bookingId));

        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!vehicle.getOwnerId().equals(ownerId)) {
            throw new DomainException("Only the vehicle owner can reject bookings");
        }

        if (booking.getStatus() != VehicleBookingStatus.PENDING_APPROVAL && booking.getStatus() != VehicleBookingStatus.PAYMENT_PENDING) {
            throw new DomainException("Booking status cannot be rejected (current: " + booking.getStatus() + ")");
        }

        // Cancel Stripe PI
        if (booking.getStripePaymentIntentId() != null) {
            try {
                paymentService.cancelPaymentIntent(booking.getStripePaymentIntentId());
            } catch (Exception e) {
                log.warn("Could not cancel PI for vehicle booking {}: {}", booking.getId(), e.getMessage());
            }
        }

        booking.setStatus(VehicleBookingStatus.REJECTED);
        booking.setRejectionReason(reason);
        bookingRepository.save(booking);
        log.info("Vehicle booking {} rejected by owner {}. Reason: {}", bookingId, ownerId, reason);
    }

    /**
     * Returns {@code true} when the vehicle has no overlapping bookings
     * in the requested date range.
     */
    @Transactional(readOnly = true)
    public boolean isAvailable(UUID vehicleId, LocalDate startDate, LocalDate endDate) {
        return !bookingRepository.existsDateOverlap(
                vehicleId, startDate, endDate, BLOCKING_STATUSES);
    }

    /**
     * Atomically validates availability and creates a booking.
     * <p>
     * Total price is computed as {@code dailyPrice × numberOfDays}
     * where a single-day rental (same start/end) counts as 1 day.
     *
     * @throws ConflictException         if the requested dates overlap an existing booking
     * @throws DomainException           if dates are invalid or the vehicle is not rentable
     * @throws ResourceNotFoundException if the vehicle does not exist
     */
    @Transactional
    public VehicleBooking reserve(UUID vehicleId, UUID tenantId,
                                  LocalDate startDate, LocalDate endDate,
                                  String message) {

        validateDates(startDate, endDate);

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + vehicleId));

        if (vehicle.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Vehicle not found: " + vehicleId);
        }
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new DomainException("Vehicle is not available for booking (status: "
                    + vehicle.getStatus() + ")");
        }
        if (vehicle.getOwnerId().equals(tenantId)) {
            throw new DomainException("Owners cannot book their own vehicle");
        }

        boolean overlaps = bookingRepository.existsDateOverlap(
                vehicleId, startDate, endDate, BLOCKING_STATUSES);
        if (overlaps) {
            throw new ConflictException(
                    "Selected dates overlap with an existing booking for this vehicle");
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal totalPrice = vehicle.getDailyPrice()
                .multiply(BigDecimal.valueOf(days));

        VehicleBooking booking = new VehicleBooking();
        booking.setVehicleId(vehicleId);
        booking.setTenantId(tenantId);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setTotalPrice(totalPrice);
        booking.setCurrency(vehicle.getCurrency());
        booking.setStatus(VehicleBookingStatus.DRAFT);
        booking.setMessage(message);

        VehicleBooking saved = bookingRepository.save(booking);
        log.info("Vehicle booking drafted: id={}, vehicle={}, tenant={}, dates={}/{}",
                saved.getId(), vehicleId, tenantId, startDate, endDate);
        return saved;
    }

    @Transactional
    public com.homeflex.core.service.PaymentService.PaymentIntentResult initiatePayment(UUID bookingId) {
        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle booking not found: " + bookingId));

        if (booking.getStatus() != VehicleBookingStatus.DRAFT && booking.getStatus() != VehicleBookingStatus.PAYMENT_FAILED) {
            throw new DomainException("Payment can only be initiated for DRAFT or PAYMENT_FAILED bookings");
        }

        String transferGroup = "vehicle_booking_" + booking.getId();
        String description = "Vehicle booking: " + booking.getId();

        com.stripe.model.PaymentIntent pi = paymentService.createBookingPaymentIntent(
                booking.getTotalPrice(), booking.getCurrency(), description, transferGroup);

        booking.setStripePaymentIntentId(pi.getId());
        booking.setStatus(VehicleBookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        log.info("Vehicle payment initiated for booking={}, pi={}", bookingId, pi.getId());
        return new com.homeflex.core.service.PaymentService.PaymentIntentResult(
                pi.getClientSecret(), pi.getId(), booking.getTotalPrice(), booking.getCurrency());
    }

    /**
     * Returns all active (non-cancelled) bookings for a vehicle, ordered by
     * start date — useful for rendering an availability calendar.
     */
    @Transactional(readOnly = true)
    public List<VehicleBooking> getActiveBookings(UUID vehicleId) {
        return bookingRepository.findByVehicleIdAndStatusInOrderByStartDateAsc(
                vehicleId, BLOCKING_STATUSES);
    }

    /**
     * Returns all bookings for a given tenant, ordered by creation date (newest first).
     */
    @Transactional(readOnly = true)
    public List<VehicleBooking> getTenantBookings(UUID tenantId) {
        return bookingRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new DomainException("Start date and end date are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new DomainException("End date must be on or after start date");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new DomainException("Start date cannot be in the past");
        }
    }
}
