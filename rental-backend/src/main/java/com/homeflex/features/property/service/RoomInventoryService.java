package com.homeflex.features.property.service;

import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.features.property.domain.entity.RoomInventory;
import com.homeflex.features.property.domain.entity.RoomType;
import com.homeflex.features.property.domain.repository.RoomInventoryRepository;
import com.homeflex.features.property.domain.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoomInventoryService {

    private final RoomInventoryRepository inventoryRepository;
    private final RoomTypeRepository roomTypeRepository;

    /**
     * Returns how many rooms are available on every date in the range.
     * Key = date, Value = available count (totalRooms - booked).
     */
    @Transactional(readOnly = true)
    public Map<LocalDate, Integer> getAvailability(UUID roomTypeId, LocalDate from, LocalDate to) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));

        Map<UUID, Integer> bookedByDate = new LinkedHashMap<>();
        inventoryRepository.findByRoomTypeIdAndDateBetween(roomTypeId, from, to)
                .forEach(ri -> bookedByDate.put(null, ri.getRoomsBooked())); // placeholder

        // Build day-by-day map
        Map<LocalDate, Integer> result = new LinkedHashMap<>();
        Map<LocalDate, Integer> bookedMap = new LinkedHashMap<>();
        inventoryRepository.findByRoomTypeIdAndDateBetween(roomTypeId, from, to)
                .forEach(ri -> bookedMap.put(ri.getDate(), ri.getRoomsBooked()));

        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            int booked = bookedMap.getOrDefault(cursor, 0);
            result.put(cursor, Math.max(0, roomType.getTotalRooms() - booked));
            cursor = cursor.plusDays(1);
        }
        return result;
    }

    /**
     * Validates inventory and reserves rooms for a booking.
     * Increments rooms_booked for each date in the range.
     */
    public void reserveRooms(UUID roomTypeId, LocalDate startDate, LocalDate endDate, int numberOfRooms) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));

        // Validate all dates have enough inventory before writing any
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            int booked = inventoryRepository.countBookedOnDate(roomTypeId, cursor);
            int available = roomType.getTotalRooms() - booked;
            if (available < numberOfRooms) {
                throw new ConflictException(
                        "Not enough rooms available on " + cursor + ": requested=" + numberOfRooms + ", available=" + available);
            }
            cursor = cursor.plusDays(1);
        }

        // Write inventory rows
        cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            RoomInventory.RoomInventoryId pk = new RoomInventory.RoomInventoryId(roomTypeId, cursor);
            RoomInventory row = inventoryRepository.findById(pk)
                    .orElse(new RoomInventory(roomTypeId, cursor, 0));
            row.setRoomsBooked(row.getRoomsBooked() + numberOfRooms);
            inventoryRepository.save(row);
            cursor = cursor.plusDays(1);
        }

        log.info("Reserved {} room(s) of type {} from {} to {}", numberOfRooms, roomTypeId, startDate, endDate);
    }

    /**
     * Releases previously reserved rooms when a booking is cancelled or rejected.
     */
    public void releaseRooms(UUID roomTypeId, LocalDate startDate, LocalDate endDate, int numberOfRooms) {
        LocalDate cursor = startDate;
        while (!cursor.isAfter(endDate)) {
            RoomInventory.RoomInventoryId pk = new RoomInventory.RoomInventoryId(roomTypeId, cursor);
            inventoryRepository.findById(pk).ifPresent(row -> {
                int updated = Math.max(0, row.getRoomsBooked() - numberOfRooms);
                if (updated == 0) {
                    inventoryRepository.delete(row);
                } else {
                    row.setRoomsBooked(updated);
                    inventoryRepository.save(row);
                }
            });
            cursor = cursor.plusDays(1);
        }

        log.info("Released {} room(s) of type {} from {} to {}", numberOfRooms, roomTypeId, startDate, endDate);
    }
}
