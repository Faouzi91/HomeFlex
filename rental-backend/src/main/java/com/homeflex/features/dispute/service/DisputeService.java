package com.homeflex.features.dispute.service;

import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.features.dispute.domain.entity.Dispute;
import com.homeflex.features.dispute.domain.repository.DisputeRepository;
import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Transactional
    public Dispute openDispute(UUID bookingId, UUID initiatorId, String reason, String description) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        User initiator = userRepository.findById(initiatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Dispute dispute = new Dispute();
        dispute.setBooking(booking);
        dispute.setInitiator(initiator);
        dispute.setReason(reason);
        dispute.setDescription(description);
        dispute.setStatus("OPEN");

        log.info("Dispute opened for booking {} by user {}", bookingId, initiatorId);
        return disputeRepository.save(dispute);
    }

    @Transactional
    public Dispute resolveDispute(UUID disputeId, UUID adminId, String resolutionNotes) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        dispute.setStatus("RESOLVED");
        dispute.setResolutionNotes(resolutionNotes);
        dispute.setResolvedAt(LocalDateTime.now());
        dispute.setResolvedBy(admin);

        log.info("Dispute {} resolved by admin {}", disputeId, adminId);
        return disputeRepository.save(dispute);
    }

    @Transactional(readOnly = true)
    public List<Dispute> getAllDisputes() {
        return disputeRepository.findAll();
    }
}
