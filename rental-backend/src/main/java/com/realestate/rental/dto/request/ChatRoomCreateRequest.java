package com.realestate.rental.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ChatRoomCreateRequest {
    @NotNull(message = "Property ID is required")
    private UUID propertyId;

    @NotNull(message = "Tenant ID is required")
    private UUID tenantId;

    @NotNull(message = "Landlord ID is required")
    private UUID landlordId;
}
