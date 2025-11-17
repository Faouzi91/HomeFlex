package com.realestate.rental.service;

import com.realestate.rental.utils.entity.Property;
import com.realestate.rental.utils.entity.User;
import com.realestate.rental.utils.enumeration.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceExtension {

    private final NotificationServiceComplete notificationService;

    public void sendPropertyApprovedNotification(User landlord, Property property) {
        String title = "Property Approved";
        String message = "Your property '" + property.getTitle() + "' has been approved and is now visible to tenants.";

        notificationService.createNotification(
                landlord.getId(),
                title,
                message,
                NotificationType.SYSTEM,
                "PROPERTY",
                property.getId()
        );

        notificationService.sendPushNotification(landlord.getId(), title, message);
    }

    public void sendPropertyRejectedNotification(User landlord, Property property, String reason) {
        String title = "Property Rejected";
        String message = "Your property '" + property.getTitle() + "' was rejected. Reason: " + reason;

        notificationService.createNotification(
                landlord.getId(),
                title,
                message,
                NotificationType.SYSTEM,
                "PROPERTY",
                property.getId()
        );

        notificationService.sendPushNotification(landlord.getId(), title, message);
    }
}
