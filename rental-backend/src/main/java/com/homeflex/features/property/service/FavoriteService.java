package com.homeflex.features.property.service;

import com.homeflex.features.property.mapper.FavoriteMapper;
import com.homeflex.features.property.mapper.PropertyMapper;
import com.homeflex.features.property.dto.response.FavoriteDto;
import com.homeflex.features.property.dto.response.PropertyDto;
import com.homeflex.features.property.domain.repository.FavoriteRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.features.property.domain.entity.Favorite;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.core.domain.entity.User;
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
    private final PropertyMapper propertyMapper;
    private final FavoriteMapper favoriteMapper;

    public FavoriteDto addToFavorites(UUID userId, UUID propertyId) {
        // Check if already favorited
        if (favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId)) {
            throw new ConflictException("Property already in favorites");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        // Create favorite
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProperty(property);
        favorite = favoriteRepository.save(favorite);

        // Update property favorite count
        property.setFavoriteCount(property.getFavoriteCount() + 1);
        propertyRepository.save(property);

        return favoriteMapper.toDto(favorite);
    }

    public void removeFromFavorites(UUID userId, UUID propertyId) {
        Favorite favorite = favoriteRepository.findByUserIdAndPropertyId(userId, propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));

        favoriteRepository.delete(favorite);

        // Update property favorite count
        Property property = favorite.getProperty();
        property.setFavoriteCount(Math.max(0, property.getFavoriteCount() - 1));
        propertyRepository.save(property);
    }

    public List<PropertyDto> getUserFavorites(UUID userId) {
        return favoriteRepository.findByUserId(userId).stream()
                .map(favorite -> propertyMapper.toDto(favorite.getProperty()))
                .collect(Collectors.toList());
    }

    public boolean isFavorite(UUID userId, UUID propertyId) {
        return favoriteRepository.existsByUserIdAndPropertyId(userId, propertyId);
    }

}
