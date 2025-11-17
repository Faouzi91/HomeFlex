package com.realestate.rental.controller;

import com.realestate.rental.dto.MessageDto;
import com.realestate.rental.service.ChatService;
import com.realestate.rental.utils.entity.TypingNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
class WebSocketChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDto message) {
        // Message is automatically sent to /topic/chat.{roomId}
        // via @SendTo annotation or SimpMessagingTemplate
    }

    @MessageMapping("/chat.typing")
    public void userTyping(@Payload TypingNotification notification) {
        // Broadcast typing indicator
    }
}