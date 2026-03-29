package com.homeflex.core.api.v1;

import com.homeflex.core.dto.response.ChatRoomDto;
import com.homeflex.core.dto.response.MessageDto;
import com.homeflex.core.dto.common.ApiListResponse;
import com.homeflex.core.dto.request.ChatRoomCreateRequest;
import com.homeflex.core.dto.request.MessageSendRequest;
import com.homeflex.core.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
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
                request.propertyId(),
                request.tenantId(),
                request.landlordId(),
                userId
        ));
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiListResponse<ChatRoomDto>> getMyChatRooms(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(new ApiListResponse<>(chatService.getChatRoomsByUser(userId)));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ApiListResponse<MessageDto>> getChatMessages(
            @PathVariable UUID roomId,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(new ApiListResponse<>(chatService.getMessagesByRoom(roomId, userId)));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<MessageDto> sendMessage(
            @PathVariable UUID roomId,
            @RequestBody MessageSendRequest request,
            Authentication authentication) {

        UUID senderId = UUID.fromString(authentication.getName());
        MessageDto message = chatService.saveMessage(roomId, senderId, request.message());

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

