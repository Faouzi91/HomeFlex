package com.homeflex.core.security;

/**
 * Compile-time constants for every permission defined in V28__RBAC_seed_and_migrate.sql.
 *
 * Usage in @PreAuthorize:
 *   hasAuthority(T(com.homeflex.core.security.Permissions).BOOKING_CREATE)
 *   hasPermission(#id, 'Booking', 'BOOKING_APPROVE')
 */
public final class Permissions {

    private Permissions() {}

    // ── User ────────────────────────────────────────────────────────────────
    public static final String USER_READ    = "USER_READ";
    public static final String USER_WRITE   = "USER_WRITE";
    public static final String USER_DELETE  = "USER_DELETE";
    public static final String USER_SUSPEND = "USER_SUSPEND";

    // ── Property ────────────────────────────────────────────────────────────
    public static final String PROPERTY_READ    = "PROPERTY_READ";
    public static final String PROPERTY_CREATE  = "PROPERTY_CREATE";
    public static final String PROPERTY_UPDATE  = "PROPERTY_UPDATE";
    public static final String PROPERTY_DELETE  = "PROPERTY_DELETE";
    public static final String PROPERTY_APPROVE = "PROPERTY_APPROVE";

    // ── Vehicle ─────────────────────────────────────────────────────────────
    public static final String VEHICLE_READ   = "VEHICLE_READ";
    public static final String VEHICLE_CREATE = "VEHICLE_CREATE";
    public static final String VEHICLE_UPDATE = "VEHICLE_UPDATE";
    public static final String VEHICLE_DELETE = "VEHICLE_DELETE";

    // ── Booking ─────────────────────────────────────────────────────────────
    public static final String BOOKING_READ    = "BOOKING_READ";
    public static final String BOOKING_CREATE  = "BOOKING_CREATE";
    public static final String BOOKING_UPDATE  = "BOOKING_UPDATE";
    public static final String BOOKING_CANCEL  = "BOOKING_CANCEL";
    public static final String BOOKING_APPROVE = "BOOKING_APPROVE";

    // ── Lease ───────────────────────────────────────────────────────────────
    public static final String LEASE_READ   = "LEASE_READ";
    public static final String LEASE_CREATE = "LEASE_CREATE";
    public static final String LEASE_SIGN   = "LEASE_SIGN";

    // ── Dispute ─────────────────────────────────────────────────────────────
    public static final String DISPUTE_READ    = "DISPUTE_READ";
    public static final String DISPUTE_CREATE  = "DISPUTE_CREATE";
    public static final String DISPUTE_RESOLVE = "DISPUTE_RESOLVE";

    // ── Review ──────────────────────────────────────────────────────────────
    public static final String REVIEW_READ     = "REVIEW_READ";
    public static final String REVIEW_CREATE   = "REVIEW_CREATE";
    public static final String REVIEW_MODERATE = "REVIEW_MODERATE";

    // ── Payment ─────────────────────────────────────────────────────────────
    public static final String PAYMENT_READ    = "PAYMENT_READ";
    public static final String PAYMENT_PROCESS = "PAYMENT_PROCESS";
    public static final String PAYMENT_REFUND  = "PAYMENT_REFUND";

    // ── Insurance ───────────────────────────────────────────────────────────
    public static final String INSURANCE_READ     = "INSURANCE_READ";
    public static final String INSURANCE_PURCHASE = "INSURANCE_PURCHASE";
    public static final String INSURANCE_MANAGE   = "INSURANCE_MANAGE";

    // ── KYC ─────────────────────────────────────────────────────────────────
    public static final String KYC_SUBMIT = "KYC_SUBMIT";
    public static final String KYC_READ   = "KYC_READ";
    public static final String KYC_APPROVE = "KYC_APPROVE";

    // ── Report ──────────────────────────────────────────────────────────────
    public static final String REPORT_CREATE  = "REPORT_CREATE";
    public static final String REPORT_RESOLVE = "REPORT_RESOLVE";

    // ── Maintenance ─────────────────────────────────────────────────────────
    public static final String MAINTENANCE_READ   = "MAINTENANCE_READ";
    public static final String MAINTENANCE_CREATE = "MAINTENANCE_CREATE";
    public static final String MAINTENANCE_UPDATE = "MAINTENANCE_UPDATE";

    // ── Admin / Monitoring ──────────────────────────────────────────────────
    public static final String ACTUATOR_READ   = "ACTUATOR_READ";
    public static final String ADMIN_DASHBOARD = "ADMIN_DASHBOARD";
    public static final String ADMIN_USERS     = "ADMIN_USERS";
    public static final String ADMIN_CONTENT   = "ADMIN_CONTENT";
}
