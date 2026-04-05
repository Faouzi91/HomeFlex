package com.homeflex.features.property.service;

import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.UserRole;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.Review;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.features.property.domain.repository.ReviewRepository;
import com.homeflex.features.property.dto.request.ReviewCreateRequest;
import com.homeflex.features.property.dto.response.ReviewDto;
import com.homeflex.features.property.mapper.ReviewMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    private User reviewer;
    private Property property;
    private Review review;
    private ReviewDto reviewDto;

    @BeforeEach
    void setUp() {
        reviewer = new User();
        reviewer.setId(UUID.randomUUID());
        reviewer.setEmail("tenant@example.com");
        reviewer.setRole(UserRole.TENANT);

        property = new Property();
        property.setId(UUID.randomUUID());
        property.setTitle("Test Property");

        review = new Review();
        review.setId(UUID.randomUUID());
        review.setProperty(property);
        review.setReviewer(reviewer);
        review.setRating(4);
        review.setComment("Great place!");
        review.setCreatedAt(LocalDateTime.now());

        reviewDto = new ReviewDto(
                review.getId(), property.getId(), null, 4, "Great place!", review.getCreatedAt()
        );
    }

    // ── createReview ──────────────────────────────────────────────────

    @Test
    void createReview_success() {
        ReviewCreateRequest request = new ReviewCreateRequest(property.getId(), 4, "Great place!");

        when(reviewRepository.findByPropertyIdAndReviewerId(property.getId(), reviewer.getId()))
                .thenReturn(Optional.empty());
        when(bookingRepository.existsByPropertyIdAndTenantIdAndStatusIn(
                eq(property.getId()), eq(reviewer.getId()), anyList()))
                .thenReturn(true);
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(userRepository.findById(reviewer.getId())).thenReturn(Optional.of(reviewer));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        ReviewDto result = reviewService.createReview(request, reviewer.getId());

        assertThat(result).isNotNull();
        assertThat(result.rating()).isEqualTo(4);
        assertThat(result.comment()).isEqualTo("Great place!");
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_alreadyReviewed_throwsConflict() {
        ReviewCreateRequest request = new ReviewCreateRequest(property.getId(), 5, "Amazing!");

        when(reviewRepository.findByPropertyIdAndReviewerId(property.getId(), reviewer.getId()))
                .thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.createReview(request, reviewer.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already reviewed");
    }

    @Test
    void createReview_noBooking_throwsDomain() {
        ReviewCreateRequest request = new ReviewCreateRequest(property.getId(), 3, "OK");

        when(reviewRepository.findByPropertyIdAndReviewerId(property.getId(), reviewer.getId()))
                .thenReturn(Optional.empty());
        when(bookingRepository.existsByPropertyIdAndTenantIdAndStatusIn(
                eq(property.getId()), eq(reviewer.getId()), anyList()))
                .thenReturn(false);

        assertThatThrownBy(() -> reviewService.createReview(request, reviewer.getId()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("only review properties where you had a booking");
    }

    @Test
    void createReview_propertyNotFound_throws() {
        ReviewCreateRequest request = new ReviewCreateRequest(UUID.randomUUID(), 4, "Nice");

        when(reviewRepository.findByPropertyIdAndReviewerId(any(), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.existsByPropertyIdAndTenantIdAndStatusIn(any(), any(), anyList()))
                .thenReturn(true);
        when(propertyRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(request, reviewer.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Property not found");
    }

    @Test
    void createReview_userNotFound_throws() {
        ReviewCreateRequest request = new ReviewCreateRequest(property.getId(), 4, "Nice");
        UUID unknownUserId = UUID.randomUUID();

        when(reviewRepository.findByPropertyIdAndReviewerId(property.getId(), unknownUserId))
                .thenReturn(Optional.empty());
        when(bookingRepository.existsByPropertyIdAndTenantIdAndStatusIn(
                eq(property.getId()), eq(unknownUserId), anyList()))
                .thenReturn(true);
        when(propertyRepository.findById(property.getId())).thenReturn(Optional.of(property));
        when(userRepository.findById(unknownUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(request, unknownUserId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    // ── getPropertyReviews ────────────────────────────────────────────

    @Test
    void getPropertyReviews_returnsMappedList() {
        when(reviewRepository.findByPropertyIdOrderByCreatedAtDesc(property.getId()))
                .thenReturn(List.of(review));
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        List<ReviewDto> result = reviewService.getPropertyReviews(property.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).rating()).isEqualTo(4);
    }

    @Test
    void getPropertyReviews_empty_returnsEmptyList() {
        when(reviewRepository.findByPropertyIdOrderByCreatedAtDesc(property.getId()))
                .thenReturn(List.of());

        List<ReviewDto> result = reviewService.getPropertyReviews(property.getId());

        assertThat(result).isEmpty();
    }

    // ── getAverageRating ──────────────────────────────────────────────

    @Test
    void getAverageRating_returnsAverage() {
        when(reviewRepository.getAverageRatingByPropertyId(property.getId()))
                .thenReturn(4.5);

        Double avg = reviewService.getAverageRating(property.getId());

        assertThat(avg).isEqualTo(4.5);
    }

    @Test
    void getAverageRating_noReviews_returnsNull() {
        when(reviewRepository.getAverageRatingByPropertyId(property.getId()))
                .thenReturn(null);

        Double avg = reviewService.getAverageRating(property.getId());

        assertThat(avg).isNull();
    }

    // ── deleteReview ──────────────────────────────────────────────────

    @Test
    void deleteReview_success() {
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        reviewService.deleteReview(review.getId(), reviewer.getId());

        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_notFound_throws() {
        when(reviewRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(UUID.randomUUID(), reviewer.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Review not found");
    }

    @Test
    void deleteReview_wrongUser_throwsUnauthorized() {
        UUID otherUserId = UUID.randomUUID();
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.deleteReview(review.getId(), otherUserId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Not authorized");
    }
}
