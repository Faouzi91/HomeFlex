package com.realestate.rental.application.booking;

import com.realestate.rental.dto.BookingDto;
import com.realestate.rental.dto.request.BookingCreateRequest;
import com.realestate.rental.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingApplicationService {

    private final BookingService bookingService;

    public BookingDto createBooking(BookingCreateRequest request, UUID tenantId) {
        return bookingService.createBooking(request, tenantId);
    }

    public List<BookingDto> getBookingsByTenant(UUID tenantId) {
        return bookingService.getBookingsByTenant(tenantId);
    }

    public List<BookingDto> getBookingsByProperty(UUID propertyId, UUID landlordId) {
        return bookingService.getBookingsByProperty(propertyId, landlordId);
    }

    public BookingDto getBookingById(UUID bookingId, UUID userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    public BookingDto approveBooking(UUID bookingId, UUID landlordId, String response) {
        return bookingService.approveBooking(bookingId, landlordId, response);
    }

    public BookingDto rejectBooking(UUID bookingId, UUID landlordId, String reason) {
        return bookingService.rejectBooking(bookingId, landlordId, reason);
    }

    public BookingDto cancelBooking(UUID bookingId, UUID tenantId) {
        return bookingService.cancelBooking(bookingId, tenantId);
    }
}
