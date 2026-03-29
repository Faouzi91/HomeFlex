package com.homeflex.features.property.service;
import com.homeflex.core.service.NotificationService;

import com.homeflex.features.property.mapper.BookingMapper;
import com.homeflex.features.property.dto.response.BookingDto;
import com.homeflex.features.property.dto.request.BookingCreateRequest;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.core.domain.entity.User;
import com.homeflex.features.property.domain.enums.BookingStatus;
import com.homeflex.features.property.domain.enums.BookingType;
import com.homeflex.core.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final BookingMapper bookingMapper;

    public BookingDto createBooking(BookingCreateRequest request, UUID tenantId) {
        // Get property
        Property property = propertyRepository.findById(request.propertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        // Get tenant
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        // Validate tenant role
        if (tenant.getRole() != UserRole.TENANT) {
            throw new UnauthorizedException("Only tenants can create bookings");
        }

        validateBookingDates(request);
        validateNoDateOverlap(request);

        // Create booking
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setTenant(tenant);
        booking.setBookingType(BookingType.valueOf(request.bookingType()));
        booking.setRequestedDate(request.requestedDate());
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setMessage(request.message());
        booking.setNumberOfOccupants(request.numberOfOccupants());
        booking.setStatus(BookingStatus.PENDING);

        booking = bookingRepository.save(booking);

        // Notify landlord
        notificationService.sendBookingRequestNotification(
                property.getLandlord().getId(),
                tenant,
                property
        );

        return bookingMapper.toDto(booking);
    }

    private void validateBookingDates(BookingCreateRequest request) {
        if (request.startDate() != null
                && request.endDate() != null
                && request.endDate().isBefore(request.startDate())) {
            throw new DomainException("End date must be on or after start date");
        }
    }

    private void validateNoDateOverlap(BookingCreateRequest request) {
        if (request.startDate() == null || request.endDate() == null) {
            return;
        }

        boolean overlaps = bookingRepository.existsDateOverlapForProperty(
                request.propertyId(),
                request.startDate(),
                request.endDate(),
                Arrays.asList(BookingStatus.PENDING, BookingStatus.APPROVED, BookingStatus.COMPLETED)
        );

        if (overlaps) {
            throw new ConflictException("Selected dates overlap with an existing booking");
        }
    }

    public List<BookingDto> getBookingsByTenant(UUID tenantId) {
        return bookingMapper.toDto(bookingRepository.findByTenantIdOrderByCreatedAtDesc(tenantId));
    }

    public List<BookingDto> getBookingsByProperty(UUID propertyId, UUID landlordId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        // Verify ownership
        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to view these bookings");
        }

        return bookingMapper.toDto(bookingRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId));
    }

    public BookingDto getBookingById(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Verify user is either tenant or landlord
        if (!booking.getTenant().getId().equals(userId) &&
                !booking.getProperty().getLandlord().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to view this booking");
        }

        return bookingMapper.toDto(booking);
    }

    public BookingDto approveBooking(UUID bookingId, UUID landlordId, String response) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Verify landlord owns the property
        if (!booking.getProperty().getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to approve this booking");
        }

        booking.setStatus(BookingStatus.APPROVED);
        booking.setLandlordResponse(response);
        booking.setRespondedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);

        // Notify tenant
        notificationService.sendBookingResponseNotification(
                booking.getTenant().getId(),
                booking.getProperty(),
                true
        );

        return bookingMapper.toDto(booking);
    }

    public BookingDto rejectBooking(UUID bookingId, UUID landlordId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Verify landlord owns the property
        if (!booking.getProperty().getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException("Not authorized to reject this booking");
        }

        booking.setStatus(BookingStatus.REJECTED);
        booking.setLandlordResponse(reason);
        booking.setRespondedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);

        // Notify tenant
        notificationService.sendBookingResponseNotification(
                booking.getTenant().getId(),
                booking.getProperty(),
                false
        );

        return bookingMapper.toDto(booking);
    }

    public BookingDto cancelBooking(UUID bookingId, UUID tenantId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Verify tenant owns the booking
        if (!booking.getTenant().getId().equals(tenantId)) {
            throw new UnauthorizedException("Not authorized to cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }
}