package com.realestate.rental.controller;

import com.realestate.rental.dto.*;
import com.realestate.rental.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{propertyId}")
    public ResponseEntity<FavoriteDto> addToFavorites(
            @PathVariable UUID propertyId,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        FavoriteDto favorite = favoriteService.addToFavorites(userId, propertyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<Void> removeFromFavorites(
            @PathVariable UUID propertyId,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        favoriteService.removeFromFavorites(userId, propertyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PropertyDto>> getMyFavorites(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(favoriteService.getUserFavorites(userId));
    }

    @GetMapping("/check/{propertyId}")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable UUID propertyId,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(favoriteService.isFavorite(userId, propertyId));
    }
}
