package com.homeflex.features.property.service;

import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.core.service.StorageService;
import com.homeflex.features.property.domain.entity.Amenity;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.RoomType;
import com.homeflex.features.property.domain.entity.RoomTypeImage;
import com.homeflex.features.property.domain.repository.AmenityRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.features.property.domain.repository.RoomTypeRepository;
import com.homeflex.features.property.dto.request.RoomTypeCreateRequest;
import com.homeflex.features.property.dto.response.AmenityDto;
import com.homeflex.features.property.dto.response.RoomTypeDto;
import com.homeflex.features.property.dto.response.RoomTypeImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final PropertyRepository propertyRepository;
    private final AmenityRepository amenityRepository;
    private final StorageService storageService;
    private final PropertyUnitService propertyUnitService;

    @Transactional(readOnly = true)
    public List<RoomTypeDto> getRoomTypes(UUID propertyId) {
        return roomTypeRepository.findByPropertyIdOrderByCreatedAtAsc(propertyId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public RoomTypeDto createRoomType(UUID propertyId, RoomTypeCreateRequest request, UUID ownerId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!property.getLandlord().getId().equals(ownerId)) {
            throw new UnauthorizedException("Not authorized to add room types to this property");
        }

        if (!property.getPropertyType().isHotelType()) {
            throw new DomainException("Room types can only be added to HOTEL, GUESTHOUSE, HOSTEL, or RESORT properties");
        }

        RoomType room = new RoomType();
        room.setProperty(property);
        room.setName(request.name());
        room.setDescription(request.description());
        room.setBedType(request.bedType());
        room.setNumBeds(request.numBeds());
        room.setMaxOccupancy(request.maxOccupancy());
        room.setPricePerNight(request.pricePerNight());
        room.setCurrency(request.currency());
        room.setTotalRooms(request.totalRooms());
        room.setSizeSqm(request.sizeSqm());
        room.setIsActive(true);

        if (request.amenityIds() != null && !request.amenityIds().isEmpty()) {
            room.setAmenities(new HashSet<>(amenityRepository.findAllById(request.amenityIds())));
        }

        room = roomTypeRepository.save(room);
        propertyUnitService.syncUnitCount(room.getId(), room.getTotalRooms());
        log.info("Created room type '{}' for property {}", room.getName(), propertyId);
        return toDto(room);
    }

    public RoomTypeDto updateRoomType(UUID propertyId, UUID roomTypeId,
                                      RoomTypeCreateRequest request, UUID ownerId) {
        RoomType room = findAndAuthorize(propertyId, roomTypeId, ownerId);

        room.setName(request.name());
        room.setDescription(request.description());
        room.setBedType(request.bedType());
        room.setNumBeds(request.numBeds());
        room.setMaxOccupancy(request.maxOccupancy());
        room.setPricePerNight(request.pricePerNight());
        room.setCurrency(request.currency());
        room.setTotalRooms(request.totalRooms());
        room.setSizeSqm(request.sizeSqm());

        if (request.amenityIds() != null) {
            room.setAmenities(new HashSet<>(amenityRepository.findAllById(request.amenityIds())));
        }

        room = roomTypeRepository.save(room);
        propertyUnitService.syncUnitCount(room.getId(), room.getTotalRooms());
        return toDto(room);
    }

    public void deleteRoomType(UUID propertyId, UUID roomTypeId, UUID ownerId) {
        RoomType room = findAndAuthorize(propertyId, roomTypeId, ownerId);
        roomTypeRepository.delete(room);
        log.info("Deleted room type {} from property {}", roomTypeId, propertyId);
    }

    public RoomTypeDto addImages(UUID propertyId, UUID roomTypeId,
                                  List<MultipartFile> images, UUID ownerId) {
        RoomType room = findAndAuthorize(propertyId, roomTypeId, ownerId);

        int startOrder = room.getImages().size();
        for (int i = 0; i < images.size(); i++) {
            String url = storageService.uploadFile(images.get(i), "room-types/images");
            RoomTypeImage img = new RoomTypeImage();
            img.setRoomType(room);
            img.setImageUrl(url);
            img.setDisplayOrder(startOrder + i);
            img.setIsPrimary(room.getImages().isEmpty() && i == 0);
            room.getImages().add(img);
        }

        return toDto(roomTypeRepository.save(room));
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private RoomType findAndAuthorize(UUID propertyId, UUID roomTypeId, UUID ownerId) {
        RoomType room = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
        if (!room.getProperty().getId().equals(propertyId)) {
            throw new ResourceNotFoundException("Room type does not belong to this property");
        }
        if (!room.getProperty().getLandlord().getId().equals(ownerId)) {
            throw new UnauthorizedException("Not authorized");
        }
        return room;
    }

    public RoomTypeDto toDto(RoomType room) {
        List<RoomTypeImageDto> images = room.getImages().stream()
                .map(img -> new RoomTypeImageDto(img.getId(), img.getImageUrl(),
                        img.getDisplayOrder(), img.getIsPrimary()))
                .sorted(java.util.Comparator.comparingInt(RoomTypeImageDto::displayOrder))
                .collect(Collectors.toList());

        List<AmenityDto> amenities = room.getAmenities().stream()
                .map(a -> new AmenityDto(a.getId(), a.getName(), a.getNameFr(), a.getIcon(), a.getCategory().name()))
                .collect(Collectors.toList());

        return new RoomTypeDto(
                room.getId(),
                room.getProperty().getId(),
                room.getName(),
                room.getDescription(),
                room.getBedType(),
                room.getNumBeds(),
                room.getMaxOccupancy(),
                room.getPricePerNight(),
                room.getCurrency(),
                room.getTotalRooms(),
                room.getSizeSqm(),
                room.getIsActive(),
                images,
                amenities,
                room.getCreatedAt()
        );
    }
}
