package com.homeflex.features.property.dto.response;

import com.homeflex.features.property.domain.enums.MaintenanceCategory;
import com.homeflex.features.property.domain.enums.MaintenancePriority;
import com.homeflex.features.property.domain.enums.MaintenanceStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class MaintenanceRequestResponse {
    private UUID id;
    private UUID propertyId;
    private String propertyTitle;
    private UUID tenantId;
    private String tenantName;
    private String title;
    private String description;
    private MaintenanceCategory category;
    private MaintenancePriority priority;
    private MaintenanceStatus status;
    private String resolutionNotes;
    private LocalDateTime resolvedAt;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
