package com.realestate.rental.application.property;

import com.realestate.rental.dto.PropertyDto;
import com.realestate.rental.dto.PropertySearchParams;
import com.realestate.rental.dto.request.PropertyCreateRequest;
import com.realestate.rental.dto.request.PropertyUpdateRequest;
import com.realestate.rental.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyApplicationService {

    private final PropertyService propertyService;

    public Page<PropertyDto> searchProperties(PropertySearchParams params, Pageable pageable) {
        return propertyService.searchProperties(params, pageable);
    }

    public PropertyDto getPropertyById(UUID id) {
        return propertyService.getPropertyById(id);
    }

    public PropertyDto createProperty(PropertyCreateRequest request,
                                      List<MultipartFile> images,
                                      List<MultipartFile> videos,
                                      UUID landlordId) {
        return propertyService.createProperty(request, images, videos, landlordId);
    }

    public PropertyDto createPropertyJson(PropertyCreateRequest request, UUID landlordId) {
        return propertyService.createProperty(request, null, null, landlordId);
    }

    public PropertyDto updateProperty(UUID id, PropertyUpdateRequest request,
                                      List<MultipartFile> images,
                                      List<MultipartFile> videos,
                                      UUID landlordId) {
        return propertyService.updateProperty(id, request, images, videos, landlordId);
    }

    public void deleteProperty(UUID id, UUID landlordId) {
        propertyService.deleteProperty(id, landlordId);
    }

    public List<PropertyDto> getPropertiesByLandlord(UUID landlordId) {
        return propertyService.getPropertiesByLandlord(landlordId);
    }

    public void incrementViewCount(UUID id) {
        propertyService.incrementViewCount(id);
    }

    public List<PropertyDto> getSimilarProperties(UUID id) {
        return propertyService.getSimilarProperties(id);
    }
}
