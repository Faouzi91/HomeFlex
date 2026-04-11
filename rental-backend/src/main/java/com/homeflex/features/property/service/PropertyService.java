package com.homeflex.features.property.service;

import com.homeflex.core.service.EventOutboxService;
import com.homeflex.core.service.KycService;
import com.homeflex.core.service.StorageService;
import com.homeflex.core.service.NotificationService;

import com.homeflex.features.property.mapper.PropertyMapper;
import com.homeflex.features.property.dto.response.PropertyDto;
import com.homeflex.features.property.dto.response.PropertySearchParams;
import com.homeflex.features.property.dto.request.PropertyCreateRequest;
import com.homeflex.features.property.dto.request.PropertyUpdateRequest;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.features.property.domain.repository.AmenityRepository;
import com.homeflex.features.property.domain.repository.PropertyImageRepository;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.features.property.domain.entity.Amenity;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.PropertyImage;
import com.homeflex.core.domain.entity.User;
import com.homeflex.features.property.domain.enums.ListingType;
import com.homeflex.features.property.domain.enums.PropertyStatus;
import com.homeflex.features.property.domain.enums.PropertyType;
import com.homeflex.core.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final StorageService storageService;
    private final NotificationService notificationService;
    private final BookingRepository bookingRepository;
    private final PropertyMapper propertyMapper;
    private final EventOutboxService eventOutboxService;
    private final KycService kycService;

    @Transactional(readOnly = true)
    public Page<PropertyDto> searchProperties(PropertySearchParams params, Pageable pageable) {
        Specification<Property> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Only approved and available properties
            predicates.add(cb.equal(root.get("status"), PropertyStatus.APPROVED));
            predicates.add(cb.equal(root.get("isAvailable"), true));

            if (params.city() != null && !params.city().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("city")),
                        "%" + params.city().toLowerCase().trim() + "%"));
            }

            if (params.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"),
                        BigDecimal.valueOf(params.minPrice())));
            }

            if (params.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"),
                        BigDecimal.valueOf(params.maxPrice())));
            }

            if (params.propertyType() != null && !params.propertyType().trim().isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("propertyType"),
                            PropertyType.valueOf(params.propertyType().toUpperCase())));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid property type
                }
            }

            if (params.bedrooms() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("bedrooms"),
                        params.bedrooms()));
            }

            if (params.bathrooms() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("bathrooms"),
                        params.bathrooms()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Property> properties = propertyRepository.findAll(spec, pageable);

        // Eagerly fetch all relationships within transaction
//        properties.forEach(property -> {
//            Hibernate.initialize(property.getImages());
//            Hibernate.initialize(property.getVideos());
//            Hibernate.initialize(property.getAmenities());
////            Hibernate.initialize(property.getLandlord());
//            property.getLandlord().getEmail();
//
//            // Defensive copies to avoid ConcurrentModificationException
//            List<PropertyImage> imagesCopy = new ArrayList<>(property.getImages());
//            List<Amenity> amenitiesCopy = new ArrayList<>(property.getAmenities());
//
//            imagesCopy.forEach(img -> {});
//            amenitiesCopy.forEach(am -> {});
//        });

        return properties.map(propertyMapper::toDto);
    }

    @Transactional(readOnly = true)
    @org.springframework.cache.annotation.Cacheable(value = com.homeflex.core.config.CacheConfig.PROPERTIES_CACHE, key = "#id")
    public PropertyDto getPropertyById(UUID id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (property.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Property not found");
        }

        // Force initialization of lazy collections
        property.getImages().size();
        property.getVideos().size();
        property.getAmenities().size();
        property.getLandlord().getEmail();

        return propertyMapper.toDto(property);
    }

    @Transactional
    public PropertyDto createProperty(PropertyCreateRequest request,
                                      List<MultipartFile> images,
                                      List<MultipartFile> videos,
                                      UUID landlordId) {
        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> new ResourceNotFoundException("Landlord not found"));

        if (landlord.getRole() != UserRole.LANDLORD && landlord.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedException("Only landlords can create properties");
        }

        kycService.requireVerified(landlordId);

        // Create property
        Property property = new Property();
        property.setLandlord(landlord);
        property.setTitle(request.title());
        property.setDescription(request.description());
        property.setPropertyType(PropertyType.valueOf(request.propertyType()));
        property.setListingType(ListingType.valueOf(request.listingType()));
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
        property.setIsAvailable(true);
        property.setAvailableFrom(request.availableFrom());
        property.setStatus(PropertyStatus.PENDING);

        property = propertyRepository.save(property);

        // Handle amenities
        if (request.amenityIds() != null && !request.amenityIds().isEmpty()) {
            Set<Amenity> amenities = new HashSet<>(
                    amenityRepository.findAllById(request.amenityIds())
            );
            property.setAmenities(amenities);
        }

        // Upload and save images
        if (images != null && !images.isEmpty()) {
            Property finalProperty = property;
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                String imageUrl = storageService.uploadFile(file, "properties");

                PropertyImage image = new PropertyImage();
                image.setProperty(finalProperty);
                image.setImageUrl(imageUrl);
                image.setDisplayOrder(i);
                image.setIsPrimary(i == 0);
                propertyImageRepository.save(image);
            }
        }

        property = propertyRepository.save(property);

        // Notify admin for approval
        notificationService.notifyAdminsNewProperty(property);

        // Enqueue for Elasticsearch indexing via the transactional outbox
        eventOutboxService.enqueue("Property", property.getId(),
                "PropertyIndexed", Map.of("action", "created"));

        // Force initialization before returning
        property.getImages().size();
        property.getVideos().size();
        property.getAmenities().size();

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

        // Check ownership
        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to update this property");
        }

        // Update fields
        if (request.title() != null) property.setTitle(request.title());
        if (request.description() != null) property.setDescription(request.description());
        if (request.price() != null) property.setPrice(request.price());
        if (request.isAvailable() != null) property.setIsAvailable(request.isAvailable());

        property = propertyRepository.save(property);

        // Enqueue for Elasticsearch re-indexing via the transactional outbox
        eventOutboxService.enqueue("Property", property.getId(),
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
}