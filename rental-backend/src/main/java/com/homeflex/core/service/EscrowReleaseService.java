package com.homeflex.core.service;

import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.enums.BookingStatus;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.vehicle.domain.entity.VehicleBooking;
import com.homeflex.features.vehicle.domain.enums.VehicleBookingStatus;
import com.homeflex.features.vehicle.domain.repository.VehicleBookingRepository;
import com.homeflex.features.vehicle.domain.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled job that releases escrowed funds to landlords/vehicle owners
 * once the booking start date has arrived and the payment was confirmed.
 *
 * Runs every hour by default.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EscrowReleaseService {

    private final BookingRepository bookingRepository;
    private final VehicleBookingRepository vehicleBookingRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    @Scheduled(fixedDelayString = "${app.escrow.release-interval-ms:3600000}")
    public void releaseEligibleEscrows() {
        releasePropertyEscrows();
        releaseVehicleEscrows();
    }

    @Transactional
    protected void releasePropertyEscrows() {
        List<Booking> eligible = bookingRepository.findEscrowReady(
                BookingStatus.APPROVED, LocalDate.now());

        if (eligible.isEmpty()) return;

        log.info("Escrow release: found {} eligible property booking(s)", eligible.size());

        for (Booking booking : eligible) {
            try {
                User landlord = booking.getProperty().getLandlord();
                if (landlord.getStripeAccountId() == null) {
                    log.warn("Skipping escrow release for booking {} — landlord {} has no Stripe account",
                            booking.getId(), landlord.getId());
                    continue;
                }

                String transferGroup = "property_booking_" + booking.getId();
                paymentService.releaseEscrow(
                        landlord.getStripeAccountId(),
                        booking.getTotalPrice(),
                        "xaf",
                        transferGroup
                );

                booking.setEscrowReleasedAt(LocalDateTime.now());
                booking.setStatus(BookingStatus.COMPLETED);
                bookingRepository.save(booking);

                log.info("Escrow released for property booking {}: landlord={}",
                        booking.getId(), landlord.getId());
            } catch (Exception e) {
                log.error("Failed to release escrow for property booking {}", booking.getId(), e);
            }
        }
    }

    @Transactional
    protected void releaseVehicleEscrows() {
        List<VehicleBooking> eligible = vehicleBookingRepository.findEscrowReady(
                VehicleBookingStatus.CONFIRMED, LocalDate.now());

        if (eligible.isEmpty()) return;

        log.info("Escrow release: found {} eligible vehicle booking(s)", eligible.size());

        for (VehicleBooking booking : eligible) {
            try {
                var vehicle = vehicleRepository.findById(booking.getVehicleId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Vehicle not found: " + booking.getVehicleId()));

                User owner = userRepository.findById(vehicle.getOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Owner not found: " + vehicle.getOwnerId()));

                if (owner.getStripeAccountId() == null) {
                    log.warn("Skipping escrow release for vehicle booking {} — owner {} has no Stripe account",
                            booking.getId(), owner.getId());
                    continue;
                }

                String transferGroup = "vehicle_booking_" + booking.getId();
                paymentService.releaseEscrow(
                        owner.getStripeAccountId(),
                        booking.getTotalPrice(),
                        booking.getCurrency(),
                        transferGroup
                );

                booking.setEscrowReleasedAt(LocalDateTime.now());
                booking.setStatus(VehicleBookingStatus.COMPLETED);
                vehicleBookingRepository.save(booking);

                log.info("Escrow released for vehicle booking {}: owner={}",
                        booking.getId(), owner.getId());
            } catch (Exception e) {
                log.error("Failed to release escrow for vehicle booking {}", booking.getId(), e);
            }
        }
    }
}
