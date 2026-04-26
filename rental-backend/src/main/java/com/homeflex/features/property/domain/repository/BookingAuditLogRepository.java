package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.BookingAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingAuditLogRepository extends JpaRepository<BookingAuditLog, UUID> {

    List<BookingAuditLog> findByBookingIdOrderByCreatedAtAsc(UUID bookingId);
}
