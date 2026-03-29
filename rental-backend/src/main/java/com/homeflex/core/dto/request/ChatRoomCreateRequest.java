package com.homeflex.core.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ChatRoomCreateRequest(
        @NotNull(message = "Property ID is required") UUID propertyId,
        @NotNull(message = "Tenant ID is required") UUID tenantId,
        @NotNull(message = "Landlord ID is required") UUID landlordId
) {}
