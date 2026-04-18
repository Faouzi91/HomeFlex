package com.homeflex.core.service;

import com.homeflex.core.mapper.ChatMapper;
import com.homeflex.core.dto.response.ChatRoomDto;
import com.homeflex.core.dto.response.MessageDto;
import com.homeflex.core.domain.repository.ChatRoomRepository;
import com.homeflex.core.domain.repository.MessageRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.core.domain.entity.ChatRoom;
import com.homeflex.core.domain.entity.Message;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.core.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final NotificationService notificationService;
    private final ChatMapper chatMapper;

    public ChatRoomDto createOrGetChatRoom(UUID propertyId, UUID tenantId,
                                           UUID landlordId, UUID requesterId) {
        // Verify requester is either tenant or landlord
        if (!requesterId.equals(tenantId) && !requesterId.equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to create this chat room");
        }

        // Check if chat room already exists
        ChatRoom chatRoom = chatRoomRepository
                .findByPropertyIdAndTenantIdAndLandlordId(propertyId, tenantId, landlordId)
                .orElseGet(() -> {
                    Property property = propertyRepository.findById(propertyId)
                            .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
                    User tenant = userRepository.findById(tenantId)
                            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
                    User landlord = userRepository.findById(landlordId)
                            .orElseThrow(() -> new ResourceNotFoundException("Landlord not found"));

                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setProperty(property);
                    newRoom.setTenant(tenant);
                    newRoom.setLandlord(landlord);
                    return chatRoomRepository.save(newRoom);
                });

        return chatMapper.toDto(chatRoom, messageRepository.countUnreadInRoom(chatRoom.getId(), requesterId));
    }

    public List<ChatRoomDto> getChatRoomsByUser(UUID userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByTenantIdOrLandlordId(userId, userId);
        return chatRooms.stream()
                .map(room -> chatMapper.toDto(room, messageRepository.countUnreadInRoom(room.getId(), userId)))
                .collect(Collectors.toList());
    }

    public List<MessageDto> getMessagesByRoom(UUID roomId, UUID userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));

        // Verify user is part of this chat
        if (!chatRoom.getTenant().getId().equals(userId) &&
                !chatRoom.getLandlord().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to view this chat");
        }

        List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId);
        return messages.stream()
                .map(chatMapper::toDto)
                .collect(Collectors.toList());
    }

    public MessageDto saveMessage(UUID roomId, UUID senderId, String messageText) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        // Verify sender is part of this chat
        if (!chatRoom.getTenant().getId().equals(senderId) &&
                !chatRoom.getLandlord().getId().equals(senderId)) {
            throw new UnauthorizedException("Not authorized to send messages in this chat");
        }

        // Create message
        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setMessageText(messageText);
        message.setIsRead(false);

        message = messageRepository.save(message);

        // Update chat room last message time
        chatRoom.setLastMessageAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);

        // Send notification to recipient
        UUID recipientId = sender.getId().equals(chatRoom.getTenant().getId())
                ? chatRoom.getLandlord().getId()
                : chatRoom.getTenant().getId();

        notificationService.sendNewMessageNotification(recipientId, sender, chatRoom.getProperty());

        return chatMapper.toDto(message);
    }

    public void markMessageAsRead(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        // Verify user is the recipient
        if (message.getSender().getId().equals(userId)) {
            return; // Can't mark own message as read
        }

        ChatRoom chatRoom = message.getChatRoom();
        if (!chatRoom.getTenant().getId().equals(userId) &&
                !chatRoom.getLandlord().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized");
        }

        message.setIsRead(true);
        message.setReadAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    public void markRoomAsRead(UUID roomId, UUID userId) {
        List<Message> unreadMessages = messageRepository.findByChatRoomIdAndIsReadFalse(roomId).stream()
                .filter(m -> !m.getSender().getId().equals(userId))
                .collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();
        unreadMessages.forEach(m -> {
            m.setIsRead(true);
            m.setReadAt(now);
        });

        messageRepository.saveAll(unreadMessages);
    }

    public int getUnreadMessageCount(UUID userId) {
        return messageRepository.countUnreadMessagesForUser(userId);
    }

}