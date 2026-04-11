package com.homeflex.features.property.service;

import com.homeflex.core.service.EventOutboxService;
import com.homeflex.core.service.KycService;
import com.homeflex.core.service.StorageService;
import com.homeflex.core.service.NotificationService;
import com.homeflex.features.property.mapper.PropertyMapper;
import com.homeflex.features.property.dto.response.PropertyDto;
import com.homeflex.features.property.dto.request.PropertyCreateRequest;
import com.homeflex.features.property.dto.request.PropertyUpdateRequest;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.AmenityRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.PropertyImage;
import com.homeflex.features.property.domain.entity.PropertyVideo;
import com.homeflex.features.property.domain.entity.Amenity;
import com.homeflex.features.property.domain.enums.PropertyStatus;
import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final AmenityRepository amenityRepository;
    private final StorageService storageService;
    private final EventOutboxService eventOutboxService;
    private final KycService kycStatusService;
    private final PropertyMapper propertyMapper;
    private final NotificationService notificationService;

    public Page<PropertyDto> getAllProperties(Pageable pageable) {
        return propertyRepository.findByStatus(PropertyStatus.APPROVED, pageable)
                .map(propertyMapper::toDto);
    }

    @Transactional
    public PropertyDto createProperty(PropertyCreateRequest request,
                                      List<MultipartFile> images,
                                      List<MultipartFile> videos,
                                      UUID landlordId) {

        // KYC Guard: Only verified landlords can publish
        if (!kycStatusService.isUserVerified(landlordId)) {
            throw new DomainException("Identity verification (KYC) required before listing properties.");
        }

        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> new ResourceNotFoundException("Landlord not found"));

        Property property = new Property();
        property.setLandlord(landlord);
        property.setTitle(request.title());
        property.setDescription(request.description());
        property.setPropertyType(request.propertyType());
        property.setListingType(request.listingType());
        property.setPrice(request.price());
        property.setCurrency(request.currency());
        property.setAddress(request.address());
        property.setCity(request.city());
        property.setStateProvince(request.stateProvince());
        property.setCountry(request.country());
        property.setPostalCode(request.postalCode());
        property.setLatitude(request.latitude());
        property.setLongitude(request.longitude());
        property.setBedrooms(request.bedrooms());
        property.setBathrooms(request.bathrooms());
        property.setAreaSqm(request.areaSqm());
        property.setFloorNumber(request.floorNumber());
        property.setTotalFloors(request.totalFloors());
        property.setCancellationPolicy(request.cancellationPolicy());
        property.setCleaningFee(request.cleaningFee());
        property.setSecurityDeposit(request.securityDeposit());
        property.setStatus(PropertyStatus.PENDING);

        // Map Amenities
        if (request.amenityIds() != null && !request.amenityIds().isEmpty()) {
            Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(request.amenityIds()));
            property.setAmenities(amenities);
        }

        // Upload Images
        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                String url = storageService.uploadFile(images.get(i), "properties/images");
                PropertyImage img = new PropertyImage();
                img.setProperty(property);
                img.setImageUrl(url);
                img.setDisplayOrder(i);
                img.setIsPrimary(i == 0);
                property.getImages().add(img);
            }
        }

        // Upload Videos
        if (videos != null) {
            for (MultipartFile video : videos) {
                String url = storageService.uploadFile(video, "properties/videos");
                PropertyVideo v = new PropertyVideo();
                v.setProperty(property);
                v.setVideoUrl(url);
                property.getVideos().add(v);
            }
        }

        property = propertyRepository.save(property);

        // Outbox event for search indexing
        eventOutboxService.saveEvent("Property", property.getId(), "PropertyCreated", Map.of("title", property.getTitle()));

        // Notify admins
        notificationService.notifyAdminsNewProperty(property);

        return propertyMapper.toDto(property);
    }

    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = com.homeflex.core.config.CacheConfig.PROPERTIES_CACHE, key = "#id")
    public PropertyDto getPropertyById(UUID id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (property.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Property not found");
        }

        // Force initialization
        property.getImages().size();
        property.getVideos().size();
        property.getAmenities().size();
        property.getLandlord().getEmail();

        return propertyMapper.toDto(property);
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = com.homeflex.core.config.CacheConfig.PROPERTIES_CACHE, key = "#id")
    public PropertyDto updateProperty(UUID id, PropertyUpdateRequest request,
                                      List<MultipartFile> images,
                                      List<MultipartFile> videos,
                                      UUID landlordId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to update this property");
        }

        if (request.title() != null) property.setTitle(request.title());
        if (request.description() != null) property.setDescription(request.description());
        if (request.price() != null) property.setPrice(request.price());
        if (request.bedrooms() != null) property.setBedrooms(request.bedrooms());
        if (request.bathrooms() != null) property.setBathrooms(request.bathrooms());
        if (request.isAvailable() != null) property.setIsAvailable(request.isAvailable());

        if (request.amenityIds() != null) {
            property.getAmenities().clear();
            property.getAmenities().addAll(amenityRepository.findAllById(request.amenityIds()));
        }

        property = propertyRepository.save(property);

        eventOutboxService.saveEvent("Property", property.getId(),
                "PropertyIndexed", Map.of("action", "updated"));

        // Force initialization
        property.getImages().size();
        property.getVideos().size();
        property.getAmenities().size();

        return propertyMapper.toDto(property);
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = com.homeflex.core.config.CacheConfig.PROPERTIES_CACHE, key = "#id")
    public void deleteProperty(UUID id, UUID landlordId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to delete this property");
        }

        property.setDeletedAt(LocalDateTime.now());
        property.setIsAvailable(false);
        propertyRepository.save(property);
    }

    @Transactional(readOnly = true)
    public List<PropertyDto> getPropertiesByLandlord(UUID landlordId) {
        List<Property> properties = propertyRepository.findByLandlordId(landlordId);

        // Force initialization
        properties.forEach(property -> {
            property.getImages().size();
            property.getVideos().size();
            property.getAmenities().size();
        });

        return properties.stream().map(propertyMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("properties", propertyRepository.count());
        stats.put("users", userRepository.count());
        stats.put("cities", propertyRepository.findDistinctCitiesCount());
        stats.put("transactions", bookingRepository.count());
        return stats;
    }

    @Transactional
    public void incrementViewCount(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        property.setViewCount(property.getViewCount() + 1);
        propertyRepository.save(property);
    }

    @Transactional(readOnly = true)
    public List<PropertyDto> getSimilarProperties(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        List<Property> similarProperties = propertyRepository.findSimilarProperties(
                property.getCity(),
                property.getPropertyType(),
                property.getPrice().multiply(BigDecimal.valueOf(0.8)),
                property.getPrice().multiply(BigDecimal.valueOf(1.2)),
                propertyId
        );

        // Force initialization
        similarProperties.forEach(p -> {
            p.getImages().size();
            p.getVideos().size();
            p.getAmenities().size();
            p.getLandlord().getEmail();
        });

        return similarProperties.stream().limit(5).map(propertyMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PropertyDto> getPropertiesByIds(List<UUID> ids) {
        List<Property> properties = propertyRepository.findAllById(ids);
        return properties.stream()
                .filter(p -> p.getDeletedAt() == null)
                .map(p -> {
                    p.getImages().size();
                    p.getAmenities().size();
                    p.getLandlord().getEmail();
                    return propertyMapper.toDto(p);
                })
                .collect(Collectors.toList());
    }
}
