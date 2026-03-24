package com.realestate.rental.service;

import com.realestate.rental.application.mapper.PropertyMapper;
import com.realestate.rental.dto.*;
import com.realestate.rental.dto.request.PropertyCreateRequest;
import com.realestate.rental.dto.request.PropertyUpdateRequest;
import com.realestate.rental.repository.*;
import com.realestate.rental.utils.entity.Amenity;
import com.realestate.rental.utils.entity.Property;
import com.realestate.rental.utils.entity.PropertyImage;
import com.realestate.rental.utils.entity.User;
import com.realestate.rental.utils.enumeration.ListingType;
import com.realestate.rental.utils.enumeration.PropertyStatus;
import com.realestate.rental.utils.enumeration.PropertyType;
import com.realestate.rental.utils.enumeration.UserRole;
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

    @Transactional(readOnly = true)
    public Page<PropertyDto> searchProperties(PropertySearchParams params, Pageable pageable) {
        Specification<Property> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Only approved and available properties
            predicates.add(cb.equal(root.get("status"), PropertyStatus.APPROVED));
            predicates.add(cb.equal(root.get("isAvailable"), true));

            if (params.getCity() != null && !params.getCity().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("city")),
                        "%" + params.getCity().toLowerCase().trim() + "%"));
            }

            if (params.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"),
                        BigDecimal.valueOf(params.getMinPrice())));
            }

            if (params.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"),
                        BigDecimal.valueOf(params.getMaxPrice())));
            }

            if (params.getPropertyType() != null && !params.getPropertyType().trim().isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("propertyType"),
                            PropertyType.valueOf(params.getPropertyType().toUpperCase())));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid property type
                }
            }

            if (params.getBedrooms() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("bedrooms"),
                        params.getBedrooms()));
            }

            if (params.getBathrooms() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("bathrooms"),
                        params.getBathrooms()));
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
    public PropertyDto getPropertyById(UUID id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (property.getDeletedAt() != null) {
            throw new RuntimeException("Property not found");
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
                .orElseThrow(() -> new RuntimeException("Landlord not found"));

        if (landlord.getRole() != UserRole.LANDLORD && landlord.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only landlords can create properties");
        }

        // Create property
        Property property = new Property();
        property.setLandlord(landlord);
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setPropertyType(PropertyType.valueOf(request.getPropertyType()));
        property.setListingType(ListingType.valueOf(request.getListingType()));
        property.setPrice(request.getPrice());
        property.setCurrency(request.getCurrency());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setStateProvince(request.getStateProvince());
        property.setCountry(request.getCountry());
        property.setPostalCode(request.getPostalCode());
        property.setLatitude(request.getLatitude());
        property.setLongitude(request.getLongitude());
        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());
        property.setAreaSqm(request.getAreaSqm());
        property.setFloorNumber(request.getFloorNumber());
        property.setTotalFloors(request.getTotalFloors());
        property.setIsAvailable(true);
        property.setAvailableFrom(request.getAvailableFrom());
        property.setStatus(PropertyStatus.PENDING);

        property = propertyRepository.save(property);

        // Handle amenities
        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
            Set<Amenity> amenities = new HashSet<>(
                    amenityRepository.findAllById(request.getAmenityIds())
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

        // Force initialization before returning
        property.getImages().size();
        property.getVideos().size();
        property.getAmenities().size();

        return propertyMapper.toDto(property);
    }

    @Transactional
    public PropertyDto updateProperty(UUID id, PropertyUpdateRequest request,
                                      List<MultipartFile> images,
                                      List<MultipartFile> videos,
                                      UUID landlordId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Check ownership
        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new RuntimeException("Not authorized to update this property");
        }

        // Update fields
        if (request.getTitle() != null) property.setTitle(request.getTitle());
        if (request.getDescription() != null) property.setDescription(request.getDescription());
        if (request.getPrice() != null) property.setPrice(request.getPrice());
        if (request.getIsAvailable() != null) property.setIsAvailable(request.getIsAvailable());

        property = propertyRepository.save(property);

        // Force initialization
        property.getImages().size();
        property.getVideos().size();
        property.getAmenities().size();

        return propertyMapper.toDto(property);
    }

    @Transactional
    public void deleteProperty(UUID id, UUID landlordId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new RuntimeException("Not authorized to delete this property");
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
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setViewCount(property.getViewCount() + 1);
        propertyRepository.save(property);
    }

    @Transactional(readOnly = true)
    public List<PropertyDto> getSimilarProperties(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

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