package com.homeflex.features.property.service;

import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.PropertyAvailability;
import com.homeflex.features.property.domain.entity.RoomInventory;
import com.homeflex.features.property.domain.entity.RoomType;
import com.homeflex.features.property.domain.repository.PropertyAvailabilityRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.features.property.domain.repository.RoomInventoryRepository;
import com.homeflex.features.property.domain.repository.RoomTypeRepository;
import com.homeflex.features.property.dto.response.OccupancyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OccupancyService {

    private final PropertyRepository propertyRepository;
    private final PropertyAvailabilityRepository availabilityRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomInventoryRepository roomInventoryRepository;

    public OccupancyResponse getOccupancy(UUID propertyId, LocalDate from, LocalDate to) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (property.getPropertyType().isHotelType()) {
            return buildHotelOccupancy(property, from, to);
        }
        return buildStandaloneOccupancy(property, from, to);
    }

    public OccupancyResponse.Summary getSummary(UUID propertyId, LocalDate from, LocalDate to) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        int totalDays = (int) (to.toEpochDay() - from.toEpochDay() + 1);

        if (property.getPropertyType().isHotelType()) {
            List<RoomType> roomTypes = roomTypeRepository.findByPropertyIdAndIsActiveTrueOrderByCreatedAtAsc(propertyId);
            int totalRoomNights = 0;
            int bookedRoomNights = 0;

            for (RoomType rt : roomTypes) {
                totalRoomNights += rt.getTotalRooms() * totalDays;
                List<RoomInventory> inv = roomInventoryRepository.findByRoomTypeIdAndDateBetween(rt.getId(), from, to);
                bookedRoomNights += inv.stream().mapToInt(RoomInventory::getRoomsBooked).sum();
            }

            double rate = totalRoomNights > 0 ? (double) bookedRoomNights / totalRoomNights * 100 : 0;
            return new OccupancyResponse.Summary(
                    propertyId, property.getPropertyType().name(),
                    from, to, totalDays, bookedRoomNights, Math.round(rate * 10) / 10.0,
                    totalRoomNights, bookedRoomNights);
        }

        // Standalone
        List<PropertyAvailability> blocked = availabilityRepository
                .findRange(propertyId, from, to);
        int occupiedDays = (int) blocked.stream()
                .filter(a -> a.getStatus() == PropertyAvailability.Status.BOOKED).count();
        double rate = totalDays > 0 ? (double) occupiedDays / totalDays * 100 : 0;

        return new OccupancyResponse.Summary(
                propertyId, property.getPropertyType().name(),
                from, to, totalDays, occupiedDays, Math.round(rate * 10) / 10.0, 0, 0);
    }

    // ── Private builders ──────────────────────────────────────────────────────

    private OccupancyResponse.Standalone buildStandaloneOccupancy(Property property, LocalDate from, LocalDate to) {
        List<PropertyAvailability> blocked = availabilityRepository
                .findRange(property.getId(), from, to);

        Map<LocalDate, PropertyAvailability> byDate = blocked.stream()
                .collect(Collectors.toMap(PropertyAvailability::getDate, a -> a));

        List<OccupancyResponse.DayStatus> days = new ArrayList<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            PropertyAvailability avail = byDate.get(cursor);
            if (avail == null) {
                days.add(new OccupancyResponse.DayStatus(cursor, "AVAILABLE", null));
            } else {
                String status = avail.getStatus() == PropertyAvailability.Status.BOOKED ? "BOOKED" : "BLOCKED";
                days.add(new OccupancyResponse.DayStatus(cursor, status, avail.getBookingId()));
            }
            cursor = cursor.plusDays(1);
        }

        return new OccupancyResponse.Standalone("STANDALONE", from, to, days);
    }

    private OccupancyResponse.Hotel buildHotelOccupancy(Property property, LocalDate from, LocalDate to) {
        List<RoomType> roomTypes = roomTypeRepository
                .findByPropertyIdAndIsActiveTrueOrderByCreatedAtAsc(property.getId());

        List<OccupancyResponse.RoomTypeOccupancy> rtOccupancies = new ArrayList<>();

        for (RoomType rt : roomTypes) {
            List<RoomInventory> inv = roomInventoryRepository
                    .findByRoomTypeIdAndDateBetween(rt.getId(), from, to);
            Map<LocalDate, Integer> bookedByDate = inv.stream()
                    .collect(Collectors.toMap(RoomInventory::getDate, RoomInventory::getRoomsBooked));

            List<OccupancyResponse.RoomDay> days = new ArrayList<>();
            LocalDate cursor = from;
            while (!cursor.isAfter(to)) {
                int booked = bookedByDate.getOrDefault(cursor, 0);
                int available = Math.max(0, rt.getTotalRooms() - booked);
                days.add(new OccupancyResponse.RoomDay(cursor, booked, available));
                cursor = cursor.plusDays(1);
            }

            rtOccupancies.add(new OccupancyResponse.RoomTypeOccupancy(
                    rt.getId(), rt.getName(), rt.getTotalRooms(), days));
        }

        return new OccupancyResponse.Hotel("HOTEL", from, to, rtOccupancies);
    }
}
