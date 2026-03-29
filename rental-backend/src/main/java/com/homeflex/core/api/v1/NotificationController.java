package com.homeflex.core.api.v1;

import com.homeflex.core.dto.response.NotificationDto;
import com.homeflex.core.dto.common.ApiListResponse;
import com.homeflex.core.dto.common.ApiValueResponse;
import com.homeflex.core.dto.request.FCMTokenRequest;
import com.homeflex.core.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiListResponse<NotificationDto>> getMyNotifications(
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        var notifications = unreadOnly
                ? notificationService.getUnreadNotifications(userId)
                : notificationService.getAllNotifications(userId);
        return ResponseEntity.ok(new ApiListResponse<>(notifications));
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
    public ResponseEntity<ApiValueResponse<String>> registerFCMToken(
            @RequestBody FCMTokenRequest request,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        notificationService.registerFCMToken(userId, request.token(), request.deviceType());
        return ResponseEntity.ok(new ApiValueResponse<>("FCM token registered"));
    }
}