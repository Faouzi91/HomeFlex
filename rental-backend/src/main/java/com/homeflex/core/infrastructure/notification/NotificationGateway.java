package com.homeflex.core.infrastructure.notification;

import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.core.domain.entity.User;

import java.util.UUID;

public interface NotificationGateway {
    void sendNewMessage(UUID recipientId, User sender, Property property);
    void sendBookingRequest(UUID landlordId, User tenant, Property property);
    void sendBookingResponse(UUID tenantId, Property property, boolean approved);
    void notifyAdminsNewProperty(Property property);
}
