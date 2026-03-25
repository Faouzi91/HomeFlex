package com.realestate.rental.service;

//import com.google.firebase.messaging.*;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import com.realestate.rental.application.mapper.NotificationMapper;
import com.realestate.rental.dto.*;
import com.realestate.rental.repository.*;
import com.realestate.rental.shared.exception.ResourceNotFoundException;
import com.realestate.rental.shared.exception.UnauthorizedException;
import com.realestate.rental.utils.entity.FcmToken;
import com.realestate.rental.utils.entity.Notification;
import com.realestate.rental.utils.entity.Property;
import com.realestate.rental.utils.entity.User;
import com.realestate.rental.utils.enumeration.NotificationType;
import com.realestate.rental.utils.enumeration.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    public void sendNewMessageNotification(UUID recipientId, User sender, Property property) {
        String title = "New Message";
        String message = sender.getFirstName() + " sent you a message about " + property.getTitle();

        createNotification(recipientId, title, message,
                NotificationType.NEW_MESSAGE, "PROPERTY", property.getId());

        sendPushNotification(recipientId, title, message);
    }

    public void sendBookingRequestNotification(UUID landlordId, User tenant, Property property) {
        String title = "New Booking Request";
        String message = tenant.getFirstName() + " requested to book " + property.getTitle();

        createNotification(landlordId, title, message,
                NotificationType.BOOKING_REQUEST, "PROPERTY", property.getId());

        sendPushNotification(landlordId, title, message);
    }

    public void sendBookingResponseNotification(UUID tenantId, Property property,
                                                boolean approved) {
        String title = approved ? "Booking Approved" : "Booking Declined";
        String message = "Your booking request for " + property.getTitle() +
                (approved ? " was approved" : " was declined");

        createNotification(tenantId, title, message,
                NotificationType.BOOKING_RESPONSE, "PROPERTY", property.getId());

        sendPushNotification(tenantId, title, message);
    }

    public void notifyAdminsNewProperty(Property property) {
        List<User> admins = userRepository.findByRole(UserRole.ADMIN);

        String title = "New Property Pending Approval";
        String message = property.getTitle() + " in " + property.getCity() +
                " needs review";

        for (User admin : admins) {
            createNotification(admin.getId(), title, message,
                    NotificationType.SYSTEM, "PROPERTY", property.getId());
            sendPushNotification(admin.getId(), title, message);
        }
    }

    private void createNotification(UUID userId, String title, String message,
                                    NotificationType type, String entityType, UUID entityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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

    private void sendPushNotification(UUID userId, String title, String body) {
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
            log.info("{} messages sent successfully", response.getSuccessCount());

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
            log.error("Error sending push notification: {}", e.getMessage());
        }
    }


    public void registerFCMToken(UUID userId, String token, String deviceType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FcmToken fcmToken = fcmTokenRepository.findByToken(token)
                .orElse(new FcmToken());

        fcmToken.setUser(user);
        fcmToken.setToken(token);
        fcmToken.setDeviceType(deviceType);

        fcmTokenRepository.save(fcmToken);
    }

    public List<NotificationDto> getAllNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized");
        }

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public void markAllAsRead(UUID userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadFalse(userId);

        notifications.forEach(n -> {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now());
        });

        notificationRepository.saveAll(notifications);
    }

    public void deleteNotification(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized");
        }

        notificationRepository.delete(notification);
    }

}
