package com.homeflex.features.property.domain.enums;

public enum PropertyStatus {
    /** Landlord is still filling out the registration wizard — not visible to admin yet. */
    DRAFT,
    PENDING,
    APPROVED,
    REJECTED,
    INACTIVE,
    /** Suspended by admin for policy violation — hidden from search. */
    SUSPENDED
}
