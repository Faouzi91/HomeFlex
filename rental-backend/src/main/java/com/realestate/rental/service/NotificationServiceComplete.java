package com.realestate.rental.service;

import com.google.firebase.messaging.*;
import com.realestate.rental.dto.*;
import com.realestate.rental.utils.entity.*;
import com.realestate.rental.repository.*;
import com.realestate.rental.utils.entity.FcmToken;
import com.realestate.rental.utils.entity.Notification;
import com.realestate.rental.utils.entity.Property;
import com.realestate.rental.utils.entity.User;
import com.realestate.rental.utils.enumeration.NotificationType;
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
@Deprecated(forRemoval = true)
public class NotificationServiceComplete {

    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    // All existing methods from previous NotificationService...

    public void sendPropertyApprovedNotification(User landlord, Property property) {
        String title = "Property Approved";
        String message = "Your property '" + property.getTitle() + "' has been approved!";

        createNotification(landlord.getId(), title, message,
                NotificationType.SYSTEM, "PROPERTY", property.getId());

        sendPushNotification(landlord.getId(), title, message);
    }

    public void sendPropertyRejectedNotification(User landlord, Property property, String reason) {
        String title = "Property Rejected";
        String message = "Your property '" + property.getTitle() + "' was rejected. Reason: " + reason;

        createNotification(landlord.getId(), title, message,
                NotificationType.SYSTEM, "PROPERTY", property.getId());

        sendPushNotification(landlord.getId(), title, message);
    }

    // Add to existing methods
    void createNotification(UUID userId, String title, String message,
                            NotificationType type, String entityType, UUID entityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(type);
        notification.setRelatedEntityType(entityType);
        notification.setRelatedEntityId(entityId);
        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    void sendPushNotification(UUID userId, String title, String body) {
        List<FcmToken> tokens = fcmTokenRepository.findByUserId(userId);

        if (tokens.isEmpty()) {
            return;
        }

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .addAllTokens(tokens.stream()
                        .map(FcmToken::getToken)
                        .collect(Collectors.toList()))
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

            // Remove invalid tokens
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        fcmTokenRepository.deleteByToken(tokens.get(i).getToken());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending push notification: " + e.getMessage());
        }
    }
}
