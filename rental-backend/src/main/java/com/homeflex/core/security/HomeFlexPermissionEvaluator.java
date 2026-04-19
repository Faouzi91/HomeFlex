package com.homeflex.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.UUID;

/**
 * Evaluates Spring Security's hasPermission(...) expressions.
 *
 * This class is intentionally thin. It handles the authentication contract:
 *   1. Null-guard on auth/args
 *   2. Authority presence check (does the user hold this permission?)
 *   3. Admin short-circuit (ROLE_ADMIN bypasses all ownership checks)
 *   4. UserId extraction
 *
 * All domain-specific ownership logic lives in ResourcePermissionService,
 * which is tested independently and can be reused for service-layer
 * defense-in-depth without importing Spring Security APIs.
 *
 * Supported controller-level expressions:
 *
 *   // Permission-only — no ownership check (collection endpoints, create)
 *   hasAuthority(T(com.homeflex.core.security.Permissions).BOOKING_CREATE)
 *
 *   // Permission + ownership (single-resource endpoints)
 *   hasPermission(#id, 'Booking', 'BOOKING_APPROVE')
 *   hasPermission(#id, 'Booking', 'BOOKING_READ')
 *   hasPermission(#propertyId, 'Property', 'BOOKING_READ')
 *   hasPermission(bookingObject, 'BOOKING_CANCEL')   // already-loaded entity
 */
@Component
@RequiredArgsConstructor
public class HomeFlexPermissionEvaluator implements PermissionEvaluator {

    private final ResourcePermissionService resourcePermissionService;

    // ── hasPermission(object, permission) — already-loaded domain object ─────

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if (auth == null || targetDomainObject == null || permission == null) return false;
        String perm = permission.toString();

        if (!hasAuthority(auth, perm)) return false;
        if (isAdmin(auth)) return true;

        return resourcePermissionService.isAllowed(currentUserId(auth), targetDomainObject, perm);
    }

    // ── hasPermission(id, 'Type', permission) — id + type string from SpEL ───

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId,
                                 String targetType, Object permission) {
        if (auth == null || targetId == null || targetType == null || permission == null) return false;
        String perm = permission.toString();

        if (!hasAuthority(auth, perm)) return false;
        if (isAdmin(auth)) return true;

        return resourcePermissionService.isAllowed(currentUserId(auth), targetType, toUUID(targetId), perm);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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
