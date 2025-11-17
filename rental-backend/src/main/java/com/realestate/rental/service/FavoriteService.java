package com.realestate.rental.service;

import com.realestate.rental.dto.*;
import com.realestate.rental.utils.entity.*;
import com.realestate.rental.repository.*;
import com.realestate.rental.utils.entity.Favorite;
import com.realestate.rental.utils.entity.Property;
import com.realestate.rental.utils.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public FavoriteDto addToFavorites(UUID userId, UUID propertyId) {
        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            throw new RuntimeException("Property already in favorites");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Create favorite
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProperty(property);
        favorite = favoriteRepository.save(favorite);

        // Update property favorite count
        property.setFavoriteCount(property.getFavoriteCount() + 1);
        propertyRepository.save(property);

        return mapToFavoriteDto(favorite);
    }

    public void removeFromFavorites(UUID userId, UUID propertyId) {
        Favorite favorite = favoriteRepository.findByUserIdAndPropertyId(userId, propertyId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));

        favoriteRepository.delete(favorite);

        // Update property favorite count
        Property property = favorite.getProperty();
        property.setFavoriteCount(Math.max(0, property.getFavoriteCount() - 1));
        propertyRepository.save(property);
    }

    public List<PropertyDto> getUserFavorites(UUID userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(favorite -> mapToPropertyDto(favorite.getProperty()))
                .collect(Collectors.toList());
    }

    public boolean isFavorite(UUID userId, UUID propertyId) {
        return favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId);
    }

    private FavoriteDto mapToFavoriteDto(Favorite favorite) {
        return FavoriteDto.builder()
                .id(favorite.getId())
                .userId(favorite.getUser().getId())
                .propertyId(favorite.getProperty().getId())
                .createdAt(favorite.getCreatedAt())
                .build();
    }

    private PropertyDto mapToPropertyDto(Property property) {
        PropertyDto dto = new PropertyDto();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setPropertyType(property.getPropertyType().name());
        dto.setPrice(property.getPrice());
        dto.setCity(property.getCity());
        dto.setCountry(property.getCountry());
        dto.setBedrooms(property.getBedrooms());
        dto.setBathrooms(property.getBathrooms());
        dto.setIsAvailable(property.getIsAvailable());
        dto.setCreatedAt(property.getCreatedAt());
        return dto;
    }
}
