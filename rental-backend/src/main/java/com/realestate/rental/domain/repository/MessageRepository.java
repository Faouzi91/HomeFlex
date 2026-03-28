package com.realestate.rental.domain.repository;

import com.realestate.rental.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

// MessageRepository.java
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByChatRoomIdOrderByCreatedAtAsc(UUID chatRoomId);

    @Query("SELECT COUNT(m) FROM Message m WHERE " +
            "m.chatRoom.id IN (SELECT cr.id FROM ChatRoom cr WHERE cr.tenant.id = :userId OR cr.landlord.id = :userId) " +
            "AND m.sender.id != :userId AND m.isRead = false")
    int countUnreadMessagesForUser(@Param("userId") UUID userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE " +
            "m.chatRoom.id = :roomId AND m.isRead = false")
    int countUnreadInRoom(@Param("roomId") UUID roomId);
}
