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
import com.homeflex.features.property.domain.enums.ReviewType;
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
        User landlord = new User();
        landlord.setId(UUID.randomUUID());
        property.setLandlord(landlord);

        review = new Review();
        review.setId(UUID.randomUUID());
        review.setType(ReviewType.PROPERTY);
        review.setProperty(property);
        review.setReviewer(reviewer);
        review.setRating(4);
        review.setComment("Great place!");
        review.setCreatedAt(LocalDateTime.now());

        reviewDto = new ReviewDto(
                review.getId(), ReviewType.PROPERTY, property.getId(), null, null, 4,
                null, null, null, null, null, null, "Great place!", null, null, review.getCreatedAt()
        );
    }

    @Test
    void createReview_success() {
        ReviewCreateRequest request = new ReviewCreateRequest(property.getId(), null, 4, null, null, null, null, null, null, "Great place!");

        when(reviewRepository.findByPropertyIdAndReviewerIdAndType(property.getId(), reviewer.getId(), ReviewType.PROPERTY))
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
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void getPropertyReviews_returnsMappedList() {
        when(reviewRepository.findByPropertyIdAndTypeOrderByCreatedAtDesc(property.getId(), ReviewType.PROPERTY))
                .thenReturn(List.of(review));
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        List<ReviewDto> result = reviewService.getPropertyReviews(property.getId());

        assertThat(result).hasSize(1);
    }

    @Test
    void getAveragePropertyRating_returnsAverage() {
        when(reviewRepository.getAverageRatingByPropertyId(property.getId()))
                .thenReturn(4.5);

        Double avg = reviewService.getAveragePropertyRating(property.getId());

        assertThat(avg).isEqualTo(4.5);
    }

    @Test
    void deleteReview_success() {
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

        reviewService.deleteReview(review.getId(), reviewer.getId());

        verify(reviewRepository).delete(review);
    }
}
