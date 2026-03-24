package com.realestate.rental.infrastructure.notification;

import com.realestate.rental.utils.entity.Property;
import com.realestate.rental.utils.entity.User;

import java.util.UUID;

public interface NotificationGateway {
    void sendNewMessage(UUID recipientId, User sender, Property property);
    void sendBookingRequest(UUID landlordId, User tenant, Property property);
    void sendBookingResponse(UUID tenantId, Property property, boolean approved);
    void notifyAdminsNewProperty(Property property);
}
