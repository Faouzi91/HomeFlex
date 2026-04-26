package com.homeflex.features.property.service;

import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.features.property.domain.entity.PropertyUnit;
import com.homeflex.features.property.domain.entity.RoomType;
import com.homeflex.features.property.domain.enums.UnitStatus;
import com.homeflex.features.property.domain.repository.PropertyUnitRepository;
import com.homeflex.features.property.domain.repository.RoomTypeRepository;
import com.homeflex.features.property.dto.request.PropertyUnitRequest;
import com.homeflex.features.property.dto.response.PropertyUnitDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PropertyUnitService {

    private final PropertyUnitRepository unitRepository;
    private final RoomTypeRepository roomTypeRepository;

    @Transactional(readOnly = true)
    public List<PropertyUnitDto> listUnits(UUID propertyId, UUID roomTypeId) {
        verifyRoomTypeBelongsToProperty(propertyId, roomTypeId);
        return unitRepository.findByRoomTypeIdOrderByUnitNumberAsc(roomTypeId)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<PropertyUnitDto> listAvailable(UUID propertyId, UUID roomTypeId,
                                               LocalDate start, LocalDate end) {
        verifyRoomTypeBelongsToProperty(propertyId, roomTypeId);
        return unitRepository.findAvailableInRange(roomTypeId, start, end)
                .stream().map(this::toDto).toList();
    }

    public PropertyUnitDto createUnit(UUID propertyId, UUID roomTypeId,
                                      PropertyUnitRequest request, UUID ownerId) {
        RoomType roomType = authorizeOwner(propertyId, roomTypeId, ownerId);

        if (unitRepository.findByRoomTypeIdOrderByUnitNumberAsc(roomTypeId)
                .stream().anyMatch(u -> u.getUnitNumber().equalsIgnoreCase(request.unitNumber()))) {
            throw new ConflictException("Unit number already exists for this room type");
        }

        PropertyUnit unit = new PropertyUnit();
        unit.setRoomType(roomType);
        unit.setUnitNumber(request.unitNumber());
        unit.setFloor(request.floor());
        unit.setStatus(parseStatus(request.status()));
        unit.setNotes(request.notes());
        unit = unitRepository.save(unit);

        log.info("Created unit '{}' for room type {}", unit.getUnitNumber(), roomTypeId);
        return toDto(unit);
    }

    public PropertyUnitDto updateUnit(UUID propertyId, UUID roomTypeId, UUID unitId,
                                      PropertyUnitRequest request, UUID ownerId) {
        authorizeOwner(propertyId, roomTypeId, ownerId);
        PropertyUnit unit = findOrThrow(unitId, roomTypeId);
        unit.setUnitNumber(request.unitNumber());
        unit.setFloor(request.floor());
        unit.setStatus(parseStatus(request.status()));
        unit.setNotes(request.notes());
        return toDto(unitRepository.save(unit));
    }

    public void deleteUnit(UUID propertyId, UUID roomTypeId, UUID unitId, UUID ownerId) {
        authorizeOwner(propertyId, roomTypeId, ownerId);
        PropertyUnit unit = findOrThrow(unitId, roomTypeId);
        unitRepository.delete(unit);
        log.info("Deleted unit {} ({})", unitId, unit.getUnitNumber());
    }

    /**
     * Auto-assignment: returns the first AVAILABLE unit with no overlapping
     * booking in the given range, or empty if all units are taken.
     */
    @Transactional(readOnly = true)
    public Optional<PropertyUnit> findFirstAvailable(UUID roomTypeId, LocalDate start, LocalDate end) {
        return unitRepository.findAvailableInRange(roomTypeId, start, end).stream().findFirst();
    }

    /**
     * Sync helper called whenever a `RoomType.totalRooms` is created or changed.
     * If totalRooms increased, append anonymous units numbered (currentMax+1)..N.
     * Does not delete existing units when totalRooms decreases — that requires
     * an explicit landlord action (via deleteUnit) to avoid orphaning bookings.
     */
    public void syncUnitCount(UUID roomTypeId, int requestedTotal) {
        long existing = unitRepository.countByRoomTypeId(roomTypeId);
        if (existing >= requestedTotal) return;

        RoomType rt = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));

        // Highest numeric unit_number so we can resume the sequence cleanly.
        int nextNumber = unitRepository.findByRoomTypeIdOrderByUnitNumberAsc(roomTypeId).stream()
                .mapToInt(u -> {
                    try { return Integer.parseInt(u.getUnitNumber()); }
                    catch (NumberFormatException e) { return 0; }
                }).max().orElse(0) + 1;

        long toCreate = requestedTotal - existing;
        for (long i = 0; i < toCreate; i++) {
            PropertyUnit u = new PropertyUnit();
            u.setRoomType(rt);
            u.setUnitNumber(String.valueOf(nextNumber + i));
            u.setStatus(UnitStatus.AVAILABLE);
            unitRepository.save(u);
        }
        log.info("Synced {} unit(s) for room type {} (existing={}, requested={})",
                toCreate, roomTypeId, existing, requestedTotal);
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private RoomType authorizeOwner(UUID propertyId, UUID roomTypeId, UUID ownerId) {
        RoomType rt = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
        if (!rt.getProperty().getId().equals(propertyId)) {
            throw new ResourceNotFoundException("Room type does not belong to this property");
        }
        if (!rt.getProperty().getLandlord().getId().equals(ownerId)) {
            throw new UnauthorizedException("Not authorized");
        }
        return rt;
    }

    private void verifyRoomTypeBelongsToProperty(UUID propertyId, UUID roomTypeId) {
        RoomType rt = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
        if (!rt.getProperty().getId().equals(propertyId)) {
            throw new ResourceNotFoundException("Room type does not belong to this property");
        }
    }

    private PropertyUnit findOrThrow(UUID unitId, UUID roomTypeId) {
        PropertyUnit u = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found"));
        if (!u.getRoomType().getId().equals(roomTypeId)) {
            throw new ResourceNotFoundException("Unit does not belong to this room type");
        }
        return u;
    }

    private UnitStatus parseStatus(String s) {
        if (s == null || s.isBlank()) return UnitStatus.AVAILABLE;
        try { return UnitStatus.valueOf(s); }
        catch (IllegalArgumentException e) { return UnitStatus.AVAILABLE; }
    }

    private PropertyUnitDto toDto(PropertyUnit u) {
        return new PropertyUnitDto(
                u.getId(),
                u.getRoomType().getId(),
                u.getUnitNumber(),
                u.getFloor(),
                u.getStatus().name(),
                u.getNotes(),
                u.getCreatedAt()
        );
    }
}
