package com.homeflex.features.property.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "room_inventory")
@IdClass(RoomInventory.RoomInventoryId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomInventory {

    @Id
    @Column(name = "room_type_id")
    private UUID roomTypeId;

    @Id
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "rooms_booked", nullable = false)
    private Integer roomsBooked = 0;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomInventoryId implements java.io.Serializable {
        private UUID roomTypeId;
        private LocalDate date;
    }
}
