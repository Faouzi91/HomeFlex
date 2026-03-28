package com.realestate.rental.service;

import com.realestate.rental.mapper.BookingMapper;
import com.realestate.rental.dto.response.*;
import com.realestate.rental.dto.request.BookingCreateRequest;
import com.realestate.rental.exception.ConflictException;
import com.realestate.rental.exception.DomainException;
import com.realestate.rental.exception.ResourceNotFoundException;
import com.realestate.rental.exception.UnauthorizedException;
import com.realestate.rental.domain.entity.*;
import com.realestate.rental.domain.repository.*;
import com.realestate.rental.domain.entity.Booking;
import com.realestate.rental.domain.entity.Property;
import com.realestate.rental.domain.entity.User;
import com.realestate.rental.domain.enums.BookingStatus;
import com.realestate.rental.domain.enums.BookingType;
import com.realestate.rental.domain.enums.UserRole;
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