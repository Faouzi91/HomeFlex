package com.homeflex.core.service;

import com.homeflex.core.infrastructure.notification.FirebaseNotificationGateway;
import com.homeflex.core.infrastructure.notification.TwilioSmsGateway;
import com.homeflex.core.mapper.NotificationMapper;
import com.homeflex.core.dto.response.NotificationDto;
import com.homeflex.core.domain.repository.FcmTokenRepository;
import com.homeflex.core.domain.repository.NotificationRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.core.domain.entity.FcmToken;
import com.homeflex.core.domain.entity.Notification;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.NotificationType;
import com.homeflex.core.domain.enums.UserRole;
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
    private final FirebaseNotificationGateway firebaseNotificationGateway;
    private final TwilioSmsGateway twilioSmsGateway;

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
        sendSmsNotification(landlordId, "HomeFlex: New booking request from " +
                tenant.getFirstName() + " for " + property.getTitle(), false);
    }

    public void sendBookingResponseNotification(UUID tenantId, Property property,
                                                boolean approved) {
        String title = approved ? "Booking Approved" : "Booking Declined";
        String message = "Your booking request for " + property.getTitle() +
                (approved ? " was approved" : " was declined");

        createNotification(tenantId, title, message,
                NotificationType.BOOKING_RESPONSE, "PROPERTY", property.getId());

        sendPushNotification(tenantId, title, message);
        sendSmsNotification(tenantId, "HomeFlex: Your booking for " +
                property.getTitle() + (approved ? " was APPROVED." : " was DECLINED."), true);
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

    public void createNotification(UUID userId, String title, String message,
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
        firebaseNotificationGateway.sendPush(userId, title, body);
    }

    private void sendSmsNotification(UUID userId, String body, boolean preferWhatsApp) {
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
                if (preferWhatsApp) {
                    twilioSmsGateway.sendWhatsApp(user.getPhoneNumber(), body);
                } else {
                    twilioSmsGateway.sendSms(user.getPhoneNumber(), body);
                }
            }
        });
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

    public void sendPropertyApprovedNotification(User landlord, Property property) {
        String title = "Property Approved";
        String message = "Your property '" + property.getTitle() + "' has been approved and is now visible to tenants.";

        createNotification(landlord.getId(), title, message,
                NotificationType.SYSTEM, "PROPERTY", property.getId());

        sendPushNotification(landlord.getId(), title, message);
    }

    public void sendReportResolvedNotification(User reporter, Property property, String notes) {
        String title = "Report Reviewed";
        String message = "Your report on '" + property.getTitle() + "' has been reviewed."
                + (notes != null ? " Resolution: " + notes : "");

        createNotification(reporter.getId(), title, message,
                NotificationType.SYSTEM, "PROPERTY", property.getId());

        sendPushNotification(reporter.getId(), title, message);
    }

    public void sendPropertyRejectedNotification(User landlord, Property property, String reason) {
        String title = "Property Rejected";
        String message = "Your property '" + property.getTitle() + "' was rejected. Reason: " + reason;

        createNotification(landlord.getId(), title, message,
                NotificationType.SYSTEM, "PROPERTY", property.getId());

        sendPushNotification(landlord.getId(), title, message);
    }

    public void sendMaintenanceRequestNotification(UUID landlordId, String tenantName, String propertyTitle, UUID propertyId) {
        String title = "New Maintenance Request";
        String message = tenantName + " has reported a new issue for " + propertyTitle;

        createNotification(landlordId, title, message,
                NotificationType.SYSTEM, "PROPERTY", propertyId);

        sendPushNotification(landlordId, title, message);
        sendSmsNotification(landlordId, "HomeFlex: New maintenance request from " +
                tenantName + " for " + propertyTitle, false);
    }

    public void sendMaintenanceStatusUpdateNotification(UUID tenantId, String titleText, String status, UUID propertyId) {
        String title = "Maintenance Request Update";
        String message = "Your maintenance request '" + titleText + "' status changed to " + status;

        createNotification(tenantId, title, message,
                NotificationType.SYSTEM, "PROPERTY", propertyId);

        sendPushNotification(tenantId, title, message);
    }
}
