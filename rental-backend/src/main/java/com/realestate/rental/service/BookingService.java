package com.realestate.rental.service;

import com.realestate.rental.application.mapper.BookingMapper;
import com.realestate.rental.dto.*;
import com.realestate.rental.dto.request.BookingCreateRequest;
import com.realestate.rental.utils.entity.*;
import com.realestate.rental.repository.*;
import com.realestate.rental.utils.entity.Booking;
import com.realestate.rental.utils.entity.Property;
import com.realestate.rental.utils.entity.User;
import com.realestate.rental.utils.enumeration.BookingStatus;
import com.realestate.rental.utils.enumeration.BookingType;
import com.realestate.rental.utils.enumeration.UserRole;
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
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Get tenant
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        // Validate tenant role
        if (tenant.getRole() != UserRole.TENANT) {
            throw new RuntimeException("Only tenants can create bookings");
        }

        validateBookingDates(request);
        validateNoDateOverlap(request);

        // Create booking
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setTenant(tenant);
        booking.setBookingType(BookingType.valueOf(request.getBookingType()));
        booking.setRequestedDate(request.getRequestedDate());
        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setMessage(request.getMessage());
        booking.setNumberOfOccupants(request.getNumberOfOccupants());
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
        if (request.getStartDate() != null
                && request.getEndDate() != null
                && request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date must be on or after start date");
        }
    }

    private void validateNoDateOverlap(BookingCreateRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return;
        }

        boolean overlaps = bookingRepository.existsDateOverlapForProperty(
                request.getPropertyId(),
                request.getStartDate(),
                request.getEndDate(),
                Arrays.asList(BookingStatus.PENDING, BookingStatus.APPROVED, BookingStatus.COMPLETED)
        );

        if (overlaps) {
            throw new RuntimeException("Selected dates overlap with an existing booking");
        }
    }

    public List<BookingDto> getBookingsByTenant(UUID tenantId) {
        return bookingMapper.toDto(bookingRepository.findByTenantIdOrderByCreatedAtDesc(tenantId));
    }

    public List<BookingDto> getBookingsByProperty(UUID propertyId, UUID landlordId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Verify ownership
        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new RuntimeException("Not authorized to view these bookings");
        }

        return bookingMapper.toDto(bookingRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId));
    }

    public BookingDto getBookingById(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify user is either tenant or landlord
        if (!booking.getTenant().getId().equals(userId) &&
                !booking.getProperty().getLandlord().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to view this booking");
        }

        return bookingMapper.toDto(booking);
    }

    public BookingDto approveBooking(UUID bookingId, UUID landlordId, String response) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify landlord owns the property
        if (!booking.getProperty().getLandlord().getId().equals(landlordId)) {
            throw new RuntimeException("Not authorized to approve this booking");
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
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify landlord owns the property
        if (!booking.getProperty().getLandlord().getId().equals(landlordId)) {
            throw new RuntimeException("Not authorized to reject this booking");
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
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Verify tenant owns the booking
        if (!booking.getTenant().getId().equals(tenantId)) {
            throw new RuntimeException("Not authorized to cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }
}