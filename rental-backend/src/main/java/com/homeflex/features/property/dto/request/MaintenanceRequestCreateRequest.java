package com.homeflex.features.property.dto.request;

import com.homeflex.features.property.domain.enums.MaintenanceCategory;
import com.homeflex.features.property.domain.enums.MaintenancePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MaintenanceRequestCreateRequest {

    @NotNull
    private UUID propertyId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private MaintenanceCategory category;

    @NotNull
    private MaintenancePriority priority;
}
