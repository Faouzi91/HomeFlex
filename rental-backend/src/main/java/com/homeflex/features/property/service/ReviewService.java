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
import com.homeflex.features.property.domain.enums.ReviewType;
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
        ReviewType type = request.propertyId() != null ? ReviewType.PROPERTY : ReviewType.TENANT;

        if (type == ReviewType.PROPERTY) {
            return createPropertyReview(request, reviewerId);
        } else {
            return createTenantReview(request, reviewerId);
        }
    }

    private ReviewDto createPropertyReview(ReviewCreateRequest request, UUID reviewerId) {
        if (reviewRepository.findByPropertyIdAndReviewerIdAndType(request.propertyId(), reviewerId, ReviewType.PROPERTY).isPresent()) {
            throw new ConflictException("You have already reviewed this property");
        }

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
        review.setType(ReviewType.PROPERTY);
        review.setProperty(property);
        review.setReviewer(reviewer);
        review.setRating(request.rating());
        review.setComment(request.comment());

        return reviewMapper.toDto(reviewRepository.save(review));
    }

    private ReviewDto createTenantReview(ReviewCreateRequest request, UUID reviewerId) {
        if (request.targetUserId() == null) {
            throw new DomainException("targetUserId is required for tenant reviews");
        }

        if (reviewRepository.findByTargetUserIdAndReviewerIdAndType(request.targetUserId(), reviewerId, ReviewType.TENANT).isPresent()) {
            throw new ConflictException("You have already reviewed this tenant");
        }

        // Verify the landlord had an approved or completed booking with this tenant
        boolean hadBooking = bookingRepository.existsByTenantIdAndPropertyLandlordIdAndStatusIn(
                request.targetUserId(), reviewerId, REVIEW_ELIGIBLE_STATUSES);
        if (!hadBooking) {
            throw new DomainException("You can only review tenants who booked your properties");
        }

        User targetUser = userRepository.findById(request.targetUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Review review = new Review();
        review.setType(ReviewType.TENANT);
        review.setTargetUser(targetUser);
        review.setReviewer(reviewer);
        review.setRating(request.rating());
        review.setComment(request.comment());

        return reviewMapper.toDto(reviewRepository.save(review));
    }

    public List<ReviewDto> getPropertyReviews(UUID propertyId) {
        return reviewRepository.findByPropertyIdAndTypeOrderByCreatedAtDesc(propertyId, ReviewType.PROPERTY).stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getTenantReviews(UUID userId) {
        return reviewRepository.findByTargetUserIdAndTypeOrderByCreatedAtDesc(userId, ReviewType.TENANT).stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    public Double getAveragePropertyRating(UUID propertyId) {
        return reviewRepository.getAverageRatingByPropertyId(propertyId);
    }

    public Double getAverageTenantRating(UUID userId) {
        return reviewRepository.getAverageRatingByUserId(userId);
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
