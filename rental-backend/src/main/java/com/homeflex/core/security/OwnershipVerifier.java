package com.homeflex.core.security;

import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.entity.Property;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Stateless ownership guard — call with already-loaded domain entities.
 * Throws AccessDeniedException (→ HTTP 403) so callers need no knowledge
 * of HTTP status codes or exception mapping.
 *
 * Design rule: this component takes loaded entities, never queries the DB.
 * The service layer is responsible for fetching the entity; this component
 * is responsible for asserting who may act on it.
 */
@Component
public class OwnershipVerifier {

    public void requireTenantOf(Booking booking, UUID currentUserId) {
        if (!booking.getTenant().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Not authorized: you are not the tenant of this booking");
        }
    }

    public void requireLandlordOf(Property property, UUID currentUserId) {
        if (!property.getLandlord().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Not authorized: you do not own this property");
        }
    }

    public void requireLandlordOfBooking(Booking booking, UUID currentUserId) {
        requireLandlordOf(booking.getProperty(), currentUserId);
    }

    public void requireTenantOrLandlordOf(Booking booking, UUID currentUserId) {
        boolean isTenant   = booking.getTenant().getId().equals(currentUserId);
        boolean isLandlord = booking.getProperty().getLandlord().getId().equals(currentUserId);
        if (!isTenant && !isLandlord) {
            throw new AccessDeniedException("Not authorized: you are not a party to this booking");
        }
    }
}
