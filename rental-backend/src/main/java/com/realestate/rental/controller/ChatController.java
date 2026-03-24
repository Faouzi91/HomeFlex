package com.realestate.rental.controller;

import com.realestate.rental.dto.*;
import com.realestate.rental.dto.request.ChatRoomCreateRequest;
import com.realestate.rental.dto.request.MessageSendRequest;
import com.realestate.rental.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomDto> createOrGetChatRoom(
            @RequestBody ChatRoomCreateRequest request,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(chatService.createOrGetChatRoom(
                request.getPropertyId(),
                request.getTenantId(),
                request.getLandlordId(),
                userId
        ));
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getMyChatRooms(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(chatService.getChatRoomsByUser(userId));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<MessageDto>> getChatMessages(
            @PathVariable UUID roomId,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(chatService.getMessagesByRoom(roomId, userId));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<MessageDto> sendMessage(
            @PathVariable UUID roomId,
            @RequestBody MessageSendRequest request,
            Authentication authentication) {

        UUID senderId = UUID.fromString(authentication.getName());
        MessageDto message = chatService.saveMessage(roomId, senderId, request.getMessage());

        // Send via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/chat." + roomId,
                message
        );

        return ResponseEntity.ok(message);
    }

    @PatchMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(
            @PathVariable UUID messageId,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        chatService.markMessageAsRead(messageId, userId);
        return ResponseEntity.ok().build();
    }
}

