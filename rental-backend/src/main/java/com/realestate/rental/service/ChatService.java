package com.realestate.rental.service;

import com.realestate.rental.dto.*;
import com.realestate.rental.repository.*;
import com.realestate.rental.utils.entity.ChatRoom;
import com.realestate.rental.utils.entity.Message;
import com.realestate.rental.utils.entity.Property;
import com.realestate.rental.utils.entity.User;
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

    public ChatRoomDto createOrGetChatRoom(UUID propertyId, UUID tenantId,
                                           UUID landlordId, UUID requesterId) {
        // Verify requester is either tenant or landlord
        if (!requesterId.equals(tenantId) && !requesterId.equals(landlordId)) {
            throw new RuntimeException("Not authorized to create this chat room");
        }

        // Check if chat room already exists
        ChatRoom chatRoom = chatRoomRepository
                .findByPropertyIdAndTenantIdAndLandlordId(propertyId, tenantId, landlordId)
                .orElseGet(() -> {
                    Property property = propertyRepository.findById(propertyId)
                            .orElseThrow(() -> new RuntimeException("Property not found"));
                    User tenant = userRepository.findById(tenantId)
                            .orElseThrow(() -> new RuntimeException("Tenant not found"));
                    User landlord = userRepository.findById(landlordId)
                            .orElseThrow(() -> new RuntimeException("Landlord not found"));

                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setProperty(property);
                    newRoom.setTenant(tenant);
                    newRoom.setLandlord(landlord);
                    return chatRoomRepository.save(newRoom);
                });

        return mapToChatRoomDto(chatRoom);
    }

    public List<ChatRoomDto> getChatRoomsByUser(UUID userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByTenantIdOrLandlordId(userId, userId);
        return chatRooms.stream()
                .map(this::mapToChatRoomDto)
                .collect(Collectors.toList());
    }

    public List<MessageDto> getMessagesByRoom(UUID roomId, UUID userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        // Verify user is part of this chat
        if (!chatRoom.getTenant().getId().equals(userId) &&
                !chatRoom.getLandlord().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to view this chat");
        }

        List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId);
        return messages.stream()
                .map(this::mapToMessageDto)
                .collect(Collectors.toList());
    }

    public MessageDto saveMessage(UUID roomId, UUID senderId, String messageText) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found"));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Verify sender is part of this chat
        if (!chatRoom.getTenant().getId().equals(senderId) &&
                !chatRoom.getLandlord().getId().equals(senderId)) {
            throw new RuntimeException("Not authorized to send messages in this chat");
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

        return mapToMessageDto(message);
    }

    public void markMessageAsRead(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        // Verify user is the recipient
        if (message.getSender().getId().equals(userId)) {
            return; // Can't mark own message as read
        }

        ChatRoom chatRoom = message.getChatRoom();
        if (!chatRoom.getTenant().getId().equals(userId) &&
                !chatRoom.getLandlord().getId().equals(userId)) {
            throw new RuntimeException("Not authorized");
        }

        message.setIsRead(true);
        message.setReadAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    public int getUnreadMessageCount(UUID userId) {
        return messageRepository.countUnreadMessagesForUser(userId);
    }

    private ChatRoomDto mapToChatRoomDto(ChatRoom chatRoom) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setId(chatRoom.getId());
        dto.setPropertyId(chatRoom.getProperty().getId());
        dto.setPropertyTitle(chatRoom.getProperty().getTitle());
        dto.setTenant(mapToUserDto(chatRoom.getTenant()));
        dto.setLandlord(mapToUserDto(chatRoom.getLandlord()));
        dto.setLastMessageAt(chatRoom.getLastMessageAt());
        dto.setUnreadCount(messageRepository.countUnreadInRoom(chatRoom.getId()));
        return dto;
    }

    private MessageDto mapToMessageDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setChatRoomId(message.getChatRoom().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFirstName() + " " +
                message.getSender().getLastName());
        dto.setMessageText(message.getMessageText());
        dto.setIsRead(message.getIsRead());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .languagePreference(user.getLanguagePreference())
                .createdAt(user.getCreatedAt())
                .build();
    }
}