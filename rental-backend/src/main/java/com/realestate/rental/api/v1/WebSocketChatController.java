package com.realestate.rental.api.v1;

import com.realestate.rental.dto.response.MessageDto;
import com.realestate.rental.service.ChatService;
import com.realestate.rental.domain.entity.TypingNotification;
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