package com.realestate.rental.service;

import com.realestate.rental.mapper.ReviewMapper;
import com.realestate.rental.dto.response.*;
import com.realestate.rental.dto.request.ReviewCreateRequest;
import com.realestate.rental.domain.repository.*;
import com.realestate.rental.exception.ConflictException;
import com.realestate.rental.exception.ResourceNotFoundException;
import com.realestate.rental.exception.UnauthorizedException;
import com.realestate.rental.domain.entity.*;
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
    private final ReviewMapper reviewMapper;

    public ReviewDto createReview(ReviewCreateRequest request, UUID reviewerId) {
        // Check if user already reviewed this property
        if (reviewRepository.findByPropertyIdAndReviewerId(request.propertyId(), reviewerId).isPresent()) {
            throw new ConflictException("You have already reviewed this property");
        }

        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = new Review();
        review.setProperty(property);
        review.setReviewer(reviewer);
        review.setRating(request.rating());
        review.setComment(request.comment());

        review = reviewRepository.save(review);

        return reviewMapper.toDto(review);
    }

    public List<ReviewDto> getPropertyReviews(UUID propertyId) {
        return reviewRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId).stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    public Double getAverageRating(UUID propertyId) {
        return reviewRepository.getAverageRatingByPropertyId(propertyId);
    }

    public void deleteReview(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getReviewer().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to delete this review");
        }

        reviewRepository.delete(review);
    }

}