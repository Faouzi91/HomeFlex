package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.RoomInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RoomInventoryRepository extends JpaRepository<RoomInventory, RoomInventory.RoomInventoryId> {

    List<RoomInventory> findByRoomTypeIdAndDateBetween(UUID roomTypeId, LocalDate from, LocalDate to);

    @Query("SELECT COALESCE(SUM(ri.roomsBooked), 0) FROM RoomInventory ri WHERE ri.roomTypeId = :roomTypeId AND ri.date = :date")
    int countBookedOnDate(UUID roomTypeId, LocalDate date);

    @Modifying
    @Query("DELETE FROM RoomInventory ri WHERE ri.roomTypeId = :roomTypeId AND ri.date BETWEEN :from AND :to")
    void deleteByRoomTypeIdAndDateBetween(UUID roomTypeId, LocalDate from, LocalDate to);
}
