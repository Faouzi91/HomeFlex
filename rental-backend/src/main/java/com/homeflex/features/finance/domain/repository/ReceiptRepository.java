package com.homeflex.features.finance.domain.repository;

import com.homeflex.features.finance.domain.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {
    List<Receipt> findByUserId(UUID userId);
    List<Receipt> findByBookingId(UUID bookingId);
}
