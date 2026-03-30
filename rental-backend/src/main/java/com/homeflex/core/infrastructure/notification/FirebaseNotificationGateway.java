package com.homeflex.core.infrastructure.notification;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import com.homeflex.core.domain.entity.FcmToken;
import com.homeflex.core.domain.repository.FcmTokenRepository;
import com.homeflex.core.service.NotificationService;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.core.domain.entity.User;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseNotificationGateway implements NotificationGateway {

    private final NotificationService notificationService;
    private final FcmTokenRepository fcmTokenRepository;
    private final CircuitBreaker firebaseCircuitBreaker;

    @Override
    public void sendNewMessage(UUID recipientId, User sender, Property property) {
        notificationService.sendNewMessageNotification(recipientId, sender, property);
    }

    @Override
    public void sendBookingRequest(UUID landlordId, User tenant, Property property) {
        notificationService.sendBookingRequestNotification(landlordId, tenant, property);
    }

    @Override
    public void sendBookingResponse(UUID tenantId, Property property, boolean approved) {
        notificationService.sendBookingResponseNotification(tenantId, property, approved);
    }

    @Override
    public void notifyAdminsNewProperty(Property property) {
        notificationService.notifyAdminsNewProperty(property);
    }

    /**
     * Sends a push notification via Firebase Cloud Messaging, protected by a circuit breaker.
     * Falls back to logging on failure so that the caller's transaction is not affected.
     */
    public void sendPush(UUID userId, String title, String body) {
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
            BatchResponse response = firebaseCircuitBreaker.executeSupplier(
                    () -> {
                        try {
                            return FirebaseMessaging.getInstance().sendMulticast(message);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            log.info("{} push messages sent successfully", response.getSuccessCount());

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
            log.warn("Push notification failed (userId={}, title={}): {}. Circuit breaker state: {}",
                    userId, title, e.getMessage(), firebaseCircuitBreaker.getState());
        }
    }
}
