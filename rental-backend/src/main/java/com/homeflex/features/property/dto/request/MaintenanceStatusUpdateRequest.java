package com.homeflex.features.property.dto.request;

import com.homeflex.features.property.domain.enums.MaintenanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaintenanceStatusUpdateRequest {

    @NotNull
    private MaintenanceStatus status;

    private String resolutionNotes;
}
