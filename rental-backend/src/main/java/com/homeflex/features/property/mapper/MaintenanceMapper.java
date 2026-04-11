package com.homeflex.features.property.mapper;

import com.homeflex.features.property.domain.entity.MaintenanceRequest;
import com.homeflex.features.property.domain.entity.MaintenanceRequestImage;
import com.homeflex.features.property.dto.response.MaintenanceRequestResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MaintenanceMapper {

    public MaintenanceRequestResponse toResponse(MaintenanceRequest request) {
        if (request == null) {
            return null;
        }
        MaintenanceRequestResponse response = new MaintenanceRequestResponse();
        response.setId(request.getId());
        response.setPropertyId(request.getProperty().getId());
        response.setPropertyTitle(request.getProperty().getTitle());
        response.setTenantId(request.getTenant().getId());
        response.setTenantName(request.getTenant().getFirstName() + " " + request.getTenant().getLastName());
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setCategory(request.getCategory());
        response.setPriority(request.getPriority());
        response.setStatus(request.getStatus());
        response.setResolutionNotes(request.getResolutionNotes());
        response.setResolvedAt(request.getResolvedAt());
        response.setImageUrls(request.getImages().stream()
                .map(MaintenanceRequestImage::getImageUrl)
                .collect(Collectors.toList()));
        response.setCreatedAt(request.getCreatedAt());
        response.setUpdatedAt(request.getUpdatedAt());
        return response;
    }
}
