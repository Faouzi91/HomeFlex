package com.realestate.rental.controller;

import com.realestate.rental.dto.*;
import com.realestate.rental.dto.api.ApiListResponse;
import com.realestate.rental.dto.api.ApiValueResponse;
import com.realestate.rental.dto.request.ReviewCreateRequest;
import com.realestate.rental.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @Valid @RequestBody ReviewCreateRequest request,
            Authentication authentication) {

        UUID reviewerId = UUID.fromString(authentication.getName());
        ReviewDto review = reviewService.createReview(request, reviewerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<ApiListResponse<ReviewDto>> getPropertyReviews(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(new ApiListResponse<>(reviewService.getPropertyReviews(propertyId)));
    }

    @GetMapping("/property/{propertyId}/average")
    public ResponseEntity<ApiValueResponse<Double>> getAverageRating(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(new ApiValueResponse<>(reviewService.getAverageRating(propertyId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }
}
