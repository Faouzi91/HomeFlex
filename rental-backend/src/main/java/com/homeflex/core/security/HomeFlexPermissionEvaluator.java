package com.homeflex.core.security;

import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

/**
 * Evaluates Spring Security's hasPermission(...) expressions.
 *
 * Supported controller-level expressions:
 *
 *   // Permission-only — no ownership check
 *   hasAuthority(T(com.homeflex.core.security.Permissions).BOOKING_CREATE)
 *
 *   // Permission + ownership — checks both in one expression
 *   hasPermission(#id, 'Booking', 'BOOKING_APPROVE')
 *   hasPermission(#propertyId, 'Property', 'PROPERTY_UPDATE')
 *
 * ADMIN role always bypasses ownership. The service layer still does its own
 * ownership verification as a second line of defense.
 */
@Component
@RequiredArgsConstructor
public class HomeFlexPermissionEvaluator implements PermissionEvaluator {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;

    // ── hasPermission(object, permission) ────────────────────────────────────
    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if (auth == null || targetDomainObject == null || permission == null) return false;
        String perm = permission.toString();

        if (!hasAuthority(auth, perm)) return false;
        if (isAdmin(auth)) return true;

        UUID userId = currentUserId(auth);

        if (targetDomainObject instanceof Booking booking) {
            return bookingOwnership(booking, userId, perm);
        }
        if (targetDomainObject instanceof Property property) {
            return property.getLandlord().getId().equals(userId);
        }
        return false;
    }

    // ── hasPermission(id, 'Type', permission) ────────────────────────────────
    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId,
                                 String targetType, Object permission) {
        if (auth == null || targetId == null || targetType == null || permission == null) return false;
        String perm = permission.toString();

        if (!hasAuthority(auth, perm)) return false;
        if (isAdmin(auth)) return true;

        UUID userId = currentUserId(auth);
        UUID resourceId = toUUID(targetId);

        return switch (targetType) {
            case "Booking" -> bookingRepository.findById(resourceId)
                    .map(b -> bookingOwnership(b, userId, perm))
                    .orElse(false);
            case "Property" -> propertyRepository.findById(resourceId)
                    .map(p -> p.getLandlord().getId().equals(userId))
                    .orElse(false);
            default -> false;
        };
    }

    // ── Ownership rules per permission ───────────────────────────────────────

    private boolean bookingOwnership(Booking booking, UUID userId, String perm) {
        boolean isTenant   = booking.getTenant().getId().equals(userId);
        boolean isLandlord = booking.getProperty().getLandlord().getId().equals(userId);

        return switch (perm) {
            case Permissions.BOOKING_APPROVE,
                 Permissions.BOOKING_UPDATE  -> isLandlord;
            case Permissions.BOOKING_CANCEL  -> isTenant;
            // Default: any party to the booking may read it
            default                          -> isTenant || isLandlord;
        };
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean hasAuthority(Authentication auth, String permission) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(permission));
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private UUID currentUserId(Authentication auth) {
        return UUID.fromString(auth.getName());
    }

    private UUID toUUID(Serializable id) {
        return id instanceof UUID uuid ? uuid : UUID.fromString(id.toString());
    }
}
