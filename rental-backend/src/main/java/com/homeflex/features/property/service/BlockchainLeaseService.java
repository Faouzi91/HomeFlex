package com.homeflex.features.property.service;

import com.homeflex.features.property.domain.entity.PropertyLease;
import com.homeflex.features.property.domain.repository.PropertyLeaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockchainLeaseService {

    private final PropertyLeaseRepository leaseRepository;

    /**
     * Simulates minting a lease NFT or recording its hash on-chain (Ethereum/Polygon).
     */
    @Async
    @Transactional
    public void recordLeaseOnChain(UUID leaseId) {
        log.info("Starting blockchain recording for lease {}", leaseId);
        
        PropertyLease lease = leaseRepository.findById(leaseId).orElse(null);
        if (lease == null) return;

        try {
            // Simulate network latency
            Thread.sleep(2000);
            
            lease.setOnChainStatus("PENDING");
            lease.setBlockchainTxHash("0x" + UUID.randomUUID().toString().replace("-", ""));
            leaseRepository.save(lease);
            
            log.info("Lease {} transaction broadcasted: {}", leaseId, lease.getBlockchainTxHash());

            // Simulate block confirmation
            Thread.sleep(3000);
            
            lease.setOnChainStatus("SUCCESS");
            lease.setContractAddress("0x71C7656EC7ab88b098defB751B7401B5f6d8976F"); // Mock contract
            lease.setTokenId(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            leaseRepository.save(lease);
            
            log.info("Lease {} successfully verified on-chain. Token ID: {}", leaseId, lease.getTokenId());
            
        } catch (Exception e) {
            log.error("Blockchain recording failed for lease {}: {}", leaseId, e.getMessage());
            lease.setOnChainStatus("FAILED");
            leaseRepository.save(lease);
        }
    }
}
