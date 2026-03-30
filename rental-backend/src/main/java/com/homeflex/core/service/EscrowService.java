package com.homeflex.core.service;

import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.enums.BookingStatus;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.vehicle.domain.entity.VehicleBooking;
import com.homeflex.features.vehicle.domain.enums.VehicleBookingStatus;
import com.homeflex.features.vehicle.domain.repository.VehicleBookingRepository;
import com.homeflex.features.vehicle.domain.repository.VehicleRepository;
import com.stripe.model.Balance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EscrowService {

    private final BookingRepository bookingRepository;
    private final VehicleBookingRepository vehicleBookingRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    // ── Scheduled Escrow Release ───────────────────────────────────────

    /**
     * Runs every hour. Finds bookings whose check-in date has been reached
     * and releases escrowed funds to the landlord's connected Stripe account.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void releaseMaturedEscrows() {
        releasePropertyEscrows();
        releaseVehicleEscrows();
    }

    @Transactional
    public void releasePropertyEscrows() {
        List<Booking> matured = bookingRepository.findEscrowReady(
                BookingStatus.APPROVED, LocalDate.now());

        for (Booking booking : matured) {
            try {
                User landlord = booking.getProperty().getLandlord();
                if (landlord.getStripeAccountId() == null) {
                    log.warn("Skipping escrow release for booking {}: landlord {} has no Stripe account",
                            booking.getId(), landlord.getId());
                    continue;
                }

                String transferGroup = "property_booking_" + booking.getId();
                var transfer = paymentService.releaseEscrow(
                        landlord.getStripeAccountId(),
                        booking.getTotalPrice(),
                        "xaf",
                        transferGroup);

                if (transfer == null) {
                    log.warn("Escrow release returned null for property booking {}; will retry next cycle",
                            booking.getId());
                    continue;
                }

                booking.setEscrowReleasedAt(LocalDateTime.now());
                bookingRepository.save(booking);

                log.info("Property escrow released: bookingId={}, landlordId={}",
                        booking.getId(), landlord.getId());
            } catch (Exception e) {
                log.error("Failed to release escrow for property booking {}", booking.getId(), e);
            }
        }
    }

    @Transactional
    public void releaseVehicleEscrows() {
        List<VehicleBooking> matured = vehicleBookingRepository.findEscrowReady(
                VehicleBookingStatus.CONFIRMED, LocalDate.now());

        for (VehicleBooking booking : matured) {
            try {
                var vehicle = vehicleRepository.findById(booking.getVehicleId())
                        .orElse(null);
                if (vehicle == null) continue;

                User owner = userRepository.findById(vehicle.getOwnerId()).orElse(null);
                if (owner == null || owner.getStripeAccountId() == null) {
                    log.warn("Skipping escrow release for vehicle booking {}: owner has no Stripe account",
                            booking.getId());
                    continue;
                }

                String transferGroup = "vehicle_booking_" + booking.getId();
                var transfer = paymentService.releaseEscrow(
                        owner.getStripeAccountId(),
                        booking.getTotalPrice(),
                        booking.getCurrency(),
                        transferGroup);

                if (transfer == null) {
                    log.warn("Escrow release returned null for vehicle booking {}; will retry next cycle",
                            booking.getId());
                    continue;
                }

                booking.setEscrowReleasedAt(LocalDateTime.now());
                vehicleBookingRepository.save(booking);

                log.info("Vehicle escrow released: bookingId={}, ownerId={}",
                        booking.getId(), owner.getId());
            } catch (Exception e) {
                log.error("Failed to release escrow for vehicle booking {}", booking.getId(), e);
            }
        }
    }

    // ── Payout Summary ─────────────────────────────────────────────────

    /**
     * Builds a payout summary for a landlord by combining Stripe balance data
     * with local booking records.
     */
    @Transactional(readOnly = true)
    public PayoutSummary getPayoutSummary(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Local escrow totals from bookings not yet released
        BigDecimal propertyEscrowHeld = bookingRepository
                .sumEscrowHeldByLandlord(userId, BookingStatus.APPROVED)
                .orElse(BigDecimal.ZERO);

        BigDecimal vehicleEscrowHeld = vehicleBookingRepository
                .sumEscrowHeldByOwner(userId, VehicleBookingStatus.CONFIRMED)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalEscrowHeld = propertyEscrowHeld.add(vehicleEscrowHeld);

        // Stripe balance (available + pending on connected account)
        BigDecimal stripeAvailable = BigDecimal.ZERO;
        BigDecimal stripePending = BigDecimal.ZERO;

        if (user.getStripeAccountId() != null) {
            Balance balance = paymentService.getConnectedAccountBalance(user.getStripeAccountId());
            if (balance != null) {
                for (Balance.Available a : balance.getAvailable()) {
                    stripeAvailable = stripeAvailable.add(
                            BigDecimal.valueOf(a.getAmount()).movePointLeft(2));
                }
                for (Balance.Pending p : balance.getPending()) {
                    stripePending = stripePending.add(
                            BigDecimal.valueOf(p.getAmount()).movePointLeft(2));
                }
            } else {
                log.warn("Could not fetch Stripe balance for user {}", userId);
            }
        }

        // Total earnings from released escrows
        BigDecimal totalPropertyEarnings = bookingRepository
                .sumReleasedByLandlord(userId)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalVehicleEarnings = vehicleBookingRepository
                .sumReleasedByOwner(userId)
                .orElse(BigDecimal.ZERO);

        BigDecimal totalEarnings = totalPropertyEarnings.add(totalVehicleEarnings);

        return new PayoutSummary(
                stripeAvailable,
                stripePending,
                totalEscrowHeld,
                totalEarnings,
                user.getStripeAccountId() != null
        );
    }

    // ── Response Record ────────────────────────────────────────────────

    public record PayoutSummary(
            BigDecimal availableBalance,
            BigDecimal pendingBalance,
            BigDecimal escrowHeld,
            BigDecimal totalEarnings,
            boolean stripeAccountConnected
    ) {}
}
