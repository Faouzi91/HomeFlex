package com.homeflex.features.property.api.v1;

import com.homeflex.features.property.dto.response.ReviewDto;
import com.homeflex.core.dto.common.ApiListResponse;
import com.homeflex.core.dto.common.ApiValueResponse;
import com.homeflex.features.property.dto.request.ReviewCreateRequest;
import com.homeflex.features.property.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<ApiValueResponse<Double>> getAveragePropertyRating(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(new ApiValueResponse<>(reviewService.getAveragePropertyRating(propertyId)));
    }

    @GetMapping("/tenant/{userId}")
    public ResponseEntity<ApiListResponse<ReviewDto>> getTenantReviews(@PathVariable UUID userId) {
        return ResponseEntity.ok(new ApiListResponse<>(reviewService.getTenantReviews(userId)));
    }

    @GetMapping("/tenant/{userId}/average")
    public ResponseEntity<ApiValueResponse<Double>> getAverageTenantRating(@PathVariable UUID userId) {
        return ResponseEntity.ok(new ApiValueResponse<>(reviewService.getAverageTenantRating(userId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID id,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reply")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<ReviewDto> replyToReview(
            @PathVariable UUID id,
            @RequestParam String reply,
            Authentication authentication) {
        UUID landlordId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(reviewService.replyToReview(id, reply, landlordId));
    }
}
