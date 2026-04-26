package com.homeflex.features.property.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public sealed interface OccupancyResponse permits OccupancyResponse.Standalone, OccupancyResponse.Hotel {

    record DayStatus(LocalDate date, String status, UUID bookingId) {}

    record RoomTypeOccupancy(
            UUID roomTypeId,
            String roomTypeName,
            int totalRooms,
            List<RoomDay> days
    ) {}

    record RoomDay(LocalDate date, int bookedRooms, int availableRooms) {}

    record Standalone(
            String type,
            LocalDate from,
            LocalDate to,
            List<DayStatus> days
    ) implements OccupancyResponse {}

    record Hotel(
            String type,
            LocalDate from,
            LocalDate to,
            List<RoomTypeOccupancy> roomTypes
    ) implements OccupancyResponse {}

    record Summary(
            UUID propertyId,
            String propertyType,
            LocalDate from,
            LocalDate to,
            int totalDays,
            int occupiedDays,
            double occupancyRate,
            int totalRoomNights,       // hotel only
            int bookedRoomNights       // hotel only
    ) {}
}
