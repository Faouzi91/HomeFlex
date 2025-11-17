package com.realestate.rental.controller;

import com.realestate.rental.dto.*;
import com.realestate.rental.dto.request.FCMTokenRequest;
import com.realestate.rental.dto.request.MessageResponse;
import com.realestate.rental.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getMyNotifications(
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        List<NotificationDto> notifications = unreadOnly
                ? notificationService.getUnreadNotifications(userId)
                : notificationService.getAllNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<MessageResponse> registerFCMToken(
            @RequestBody FCMTokenRequest request,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        notificationService.registerFCMToken(userId, request.getToken(), request.getDeviceType());
        return ResponseEntity.ok(new MessageResponse("FCM token registered"));
    }
}