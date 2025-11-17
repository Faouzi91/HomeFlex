package com.realestate.rental.service;

import com.realestate.rental.dto.*;
import com.realestate.rental.dto.request.ReviewCreateRequest;
import com.realestate.rental.utils.entity.*;
import com.realestate.rental.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public ReviewDto createReview(ReviewCreateRequest request, UUID reviewerId) {
        // Check if user already reviewed this property
        if (reviewRepository.findByPropertyIdAndReviewerId(request.getPropertyId(), reviewerId).isPresent()) {
            throw new RuntimeException("You have already reviewed this property");
        }

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review review = new Review();
        review.setProperty(property);
        review.setReviewer(reviewer);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);

        return mapToReviewDto(review);
    }

    public List<ReviewDto> getPropertyReviews(UUID propertyId) {
        return reviewRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId).stream()
                .map(this::mapToReviewDto)
                .collect(Collectors.toList());
    }

    public Double getAverageRating(UUID propertyId) {
        return reviewRepository.getAverageRatingByPropertyId(propertyId);
    }

    public void deleteReview(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getReviewer().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this review");
        }

        reviewRepository.delete(review);
    }

    private ReviewDto mapToReviewDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .propertyId(review.getProperty().getId())
                .reviewer(mapToUserDto(review.getReviewer()))
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }
}