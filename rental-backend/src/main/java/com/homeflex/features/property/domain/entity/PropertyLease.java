package com.homeflex.features.property.domain.entity;

import com.homeflex.core.domain.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "property_leases")
@Data
public class PropertyLease {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id")
    private User tenant;

    @Column(name = "lease_url", nullable = false)
    private String leaseUrl;

    @Column(name = "status", nullable = false)
    private String status; // PENDING, SIGNED, EXPIRED, CANCELLED

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "blockchain_tx_hash")
    private String blockchainTxHash;

    @Column(name = "on_chain_status")
    private String onChainStatus = "NOT_MINTED";

    @Column(name = "contract_address")
    private String contractAddress;

    @Column(name = "token_id")
    private String tokenId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
