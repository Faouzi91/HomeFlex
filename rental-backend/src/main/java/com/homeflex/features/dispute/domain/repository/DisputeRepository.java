package com.homeflex.features.dispute.domain.repository;

import com.homeflex.features.dispute.domain.entity.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DisputeRepository extends JpaRepository<Dispute, UUID> {
    List<Dispute> findByBookingId(UUID bookingId);
    List<Dispute> findByInitiatorIdOrderByCreatedAtDesc(UUID initiatorId);
}
