package com.homeflex.features.dispute.domain.repository;

import com.homeflex.features.dispute.domain.entity.DisputeEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DisputeEvidenceRepository extends JpaRepository<DisputeEvidence, UUID> {
    List<DisputeEvidence> findByDisputeId(UUID disputeId);
}
