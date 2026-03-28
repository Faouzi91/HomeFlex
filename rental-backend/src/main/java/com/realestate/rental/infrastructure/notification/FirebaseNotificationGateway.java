package com.realestate.rental.infrastructure.notification;

import com.realestate.rental.service.NotificationService;
import com.realestate.rental.domain.entity.Property;
import com.realestate.rental.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FirebaseNotificationGateway implements NotificationGateway {

    private final NotificationService notificationService;

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
}
