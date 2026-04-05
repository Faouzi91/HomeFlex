package com.homeflex.features.property.service;

import com.homeflex.features.property.mapper.ReviewMapper;
import com.homeflex.features.property.dto.response.ReviewDto;
import com.homeflex.features.property.dto.request.ReviewCreateRequest;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.ReviewRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.Review;
import com.homeflex.features.property.domain.enums.BookingStatus;
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
public class ReviewService {

    private static final List<BookingStatus> REVIEW_ELIGIBLE_STATUSES =
            List.of(BookingStatus.APPROVED, BookingStatus.COMPLETED);

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public ReviewDto createReview(ReviewCreateRequest request, UUID reviewerId) {
        // Check if user already reviewed this property
        if (reviewRepository.findByPropertyIdAndReviewerId(request.propertyId(), reviewerId).isPresent()) {
            throw new ConflictException("You have already reviewed this property");
        }

        // Verify the reviewer had an approved or completed booking for this property
        boolean hadBooking = bookingRepository.existsByPropertyIdAndTenantIdAndStatusIn(
                request.propertyId(), reviewerId, REVIEW_ELIGIBLE_STATUSES);
        if (!hadBooking) {
            throw new DomainException("You can only review properties where you had a booking");
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