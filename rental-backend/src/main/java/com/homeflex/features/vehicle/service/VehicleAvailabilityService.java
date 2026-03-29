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
            List.of(VehicleBookingStatus.PENDING, VehicleBookingStatus.CONFIRMED);

    private final VehicleRepository vehicleRepository;
    private final VehicleBookingRepository bookingRepository;

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
        booking.setStatus(VehicleBookingStatus.PENDING);
        booking.setMessage(message);

        VehicleBooking saved = bookingRepository.save(booking);
        log.info("Vehicle booking created: id={}, vehicle={}, tenant={}, dates={}/{}",
                saved.getId(), vehicleId, tenantId, startDate, endDate);
        return saved;
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
