package com.homeflex.features.property.dto.response;

import com.homeflex.core.dto.response.UserDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingDto(
        UUID id,
        PropertyDto property,
        UserDto tenant,
        String bookingType,
        LocalDateTime requestedDate,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        String message,
        Integer numberOfOccupants,
        BigDecimal totalPrice,
        BigDecimal platformFee,
        BigDecimal cleaningFee,
        BigDecimal taxAmount,
        String stripePaymentIntentId,
        String stripeClientSecret,
        String paymentStatus,
        String paymentFailureReason,
        String landlordResponse,
        LocalDateTime respondedAt,
        LocalDate proposedStartDate,
        LocalDate proposedEndDate,
        String modificationReason,
        LocalDateTime createdAt,
        // Hotel room fields
        UUID roomTypeId,
        String roomTypeName,
        Integer numberOfRooms,
        LocalDateTime paymentConfirmedAt,
        LocalDateTime escrowReleasedAt
) {}
