package com.realestate.rental.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final StorageService storageService;
    private final NotificationService notificationService;

    public Page<PropertyDto> searchProperties(PropertySearchParams params, Pageable pageable) {
        Specification<Property> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Only approved and available properties
            predicates.add(cb.equal(root.get("status"), PropertyStatus.APPROVED));
            predicates.add(cb.equal(root.get("isAvailable"), true));

            if (params.getCity() != null) {
                predicates.add(cb.like(cb.lower(root.get("city")),
                        "%" + params.getCity().toLowerCase() + "%"));
            }

            if (params.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"),
                        BigDecimal.valueOf(params.getMinPrice())));
            }

            if (params.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"),
                        BigDecimal.valueOf(params.getMaxPrice())));
            }

            if (params.getPropertyType() != null) {
                predicates.add(cb.equal(root.get("propertyType"),
                        PropertyType.valueOf(params.getPropertyType())));
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

        return propertyRepository.findAll(spec, pageable)
                .map(this::mapToPropertyDto);
    }

    public PropertyDto getPropertyById(UUID id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        return mapToPropertyDto(property);
    }

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

        // Upload videos (similar to images)

        property = propertyRepository.save(property);

        // Notify admin for approval
        notificationService.notifyAdminsNewProperty(property);

        return mapToPropertyDto(property);
    }

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
        // Update other fields...

        property = propertyRepository.save(property);

        return mapToPropertyDto(property);
    }

    public void deleteProperty(UUID id, UUID landlordId) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new RuntimeException("Not authorized to delete this property");
        }

        propertyRepository.delete(property);
    }

    public List<PropertyDto> getPropertiesByLandlord(UUID landlordId) {
        return propertyRepository.findByLandlordId(landlordId).stream()
                .map(this::mapToPropertyDto)
                .collect(Collectors.toList());
    }

    public void incrementViewCount(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setViewCount(property.getViewCount() + 1);
        propertyRepository.save(property);
    }

    public List<PropertyDto> getSimilarProperties(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Find similar properties based on type, city, and price range
        return propertyRepository.findSimilarProperties(
                        property.getCity(),
                        property.getPropertyType(),
                        property.getPrice().multiply(BigDecimal.valueOf(0.8)),
                        property.getPrice().multiply(BigDecimal.valueOf(1.2)),
                        propertyId
                ).stream()
                .limit(5)
                .map(this::mapToPropertyDto)
                .collect(Collectors.toList());
    }

    private PropertyDto mapToPropertyDto(Property property) {
        PropertyDto dto = new PropertyDto();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setPropertyType(property.getPropertyType().name());
        dto.setListingType(property.getListingType().name());
        dto.setPrice(property.getPrice());
        dto.setCurrency(property.getCurrency());
        dto.setAddress(property.getAddress());
        dto.setCity(property.getCity());
        dto.setCountry(property.getCountry());
        dto.setLatitude(property.getLatitude());
        dto.setLongitude(property.getLongitude());
        dto.setBedrooms(property.getBedrooms());
        dto.setBathrooms(property.getBathrooms());
        dto.setAreaSqm(property.getAreaSqm());
        dto.setIsAvailable(property.getIsAvailable());
        dto.setStatus(property.getStatus().name());
        dto.setViewCount(property.getViewCount());
        dto.setCreatedAt(property.getCreatedAt());

        // Map images
        dto.setImages(property.getImages().stream()
                .sorted(Comparator.comparing(PropertyImage::getDisplayOrder))
                .map(this::mapToImageDto)
                .collect(Collectors.toList()));

        // Map amenities
        dto.setAmenities(property.getAmenities().stream()
                .map(this::mapToAmenityDto)
                .collect(Collectors.toList()));

        // Map landlord
        dto.setLandlord(mapToUserDto(property.getLandlord()));

        return dto;
    }

    private PropertyImageDto mapToImageDto(PropertyImage image) {
        PropertyImageDto dto = new PropertyImageDto();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setThumbnailUrl(image.getThumbnailUrl());
        dto.setIsPrimary(image.getIsPrimary());
        return dto;
    }

    private AmenityDto mapToAmenityDto(Amenity amenity) {
        AmenityDto dto = new AmenityDto();
        dto.setId(amenity.getId());
        dto.setName(amenity.getName());
        dto.setNameFr(amenity.getNameFr());
        dto.setIcon(amenity.getIcon());
        dto.setCategory(amenity.getCategory().name());
        return dto;
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .languagePreference(user.getLanguagePreference())
                .createdAt(user.getCreatedAt())
                .build();
    }
}