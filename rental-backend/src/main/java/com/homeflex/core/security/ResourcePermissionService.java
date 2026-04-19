package com.homeflex.core.security;

import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Central ownership-rule engine.
 *
 * Separation of concerns:
 *   HomeFlexPermissionEvaluator — parses SpEL args, verifies authority presence, admin bypass
 *   ResourcePermissionService   — loads domain objects, applies per-permission ownership rules
 *
 * Two entry points are provided:
 *   isAllowed(userId, targetType, targetId, permission) — used by the id-based SpEL overload;
 *       issues one optimized query that JOIN FETCHes tenant + property.landlord.
 *   isAllowed(userId, domainObject, permission) — used when the entity is already loaded
 *       (object-based SpEL overload, or service-layer defense-in-depth).
 *
 * Performance: the id-based path calls findByIdWithParties which eagerly fetches the
 * two associations needed for ownership. This prevents lazy-load round-trips that the
 * plain findById would cause. Property lookups use the standard findById since only
 * the landlord id is needed and that association is typically fetched with the entity.
 *
 * Extending: to support a new domain type (e.g. Vehicle), add a case to isAllowed and
 * a private ownership method. No changes required in HomeFlexPermissionEvaluator.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ResourcePermissionService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;

    // ── Entry point: id-based (used from PermissionEvaluator) ─────────────────

    public boolean isAllowed(UUID userId, String targetType, UUID targetId, String permission) {
        return switch (targetType) {
            case "Booking" -> bookingRepository.findByIdWithParties(targetId)
                    .map(b -> isAllowed(userId, b, permission))
                    .orElse(false);
            case "Property" -> propertyRepository.findById(targetId)
                    .map(p -> isAllowed(userId, p, permission))
                    .orElse(false);
            default -> false;
        };
    }

    // ── Entry point: object-based (PermissionEvaluator + service defense-in-depth) ──

    public boolean isAllowed(UUID userId, Object domainObject, String permission) {
        if (domainObject instanceof Booking b)  return bookingOwnership(userId, b, permission);
        if (domainObject instanceof Property p) return propertyOwnership(userId, p, permission);
        return false;
    }

    // ── Ownership rules per domain type ───────────────────────────────────────

    private boolean bookingOwnership(UUID userId, Booking booking, String permission) {
        boolean isTenant   = booking.getTenant().getId().equals(userId);
        boolean isLandlord = booking.getProperty().getLandlord().getId().equals(userId);

        return switch (permission) {
            case Permissions.BOOKING_APPROVE,
                 Permissions.BOOKING_UPDATE  -> isLandlord;
            case Permissions.BOOKING_CANCEL  -> isTenant;
            // BOOKING_READ and any other permission: either party to the booking
            default                          -> isTenant || isLandlord;
        };
    }

    private boolean propertyOwnership(UUID userId, Property property, String permission) {
        // All property-scoped checks currently require landlord ownership.
        // When read-only agency roles are introduced, extend this switch.
        return property.getLandlord().getId().equals(userId);
    }
}
