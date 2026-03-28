package com.realestate.rental.domain.repository;

import com.realestate.rental.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// ChatRoomRepository.java
@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    Optional<ChatRoom> findByPropertyIdAndTenantIdAndLandlordId(
            UUID propertyId, UUID tenantId, UUID landlordId);

    List<ChatRoom> findByTenantIdOrLandlordId(UUID tenantId, UUID landlordId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.tenant.id = :userId OR cr.landlord.id = :userId " +
            "ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findByUserIdOrderByLastMessageAtDesc(@Param("userId") UUID userId);
}
