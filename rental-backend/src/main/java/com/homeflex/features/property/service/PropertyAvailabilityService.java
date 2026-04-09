package com.homeflex.features.property.service;

import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.exception.UnauthorizedException;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.PropertyAvailability;
import com.homeflex.features.property.domain.repository.PropertyAvailabilityRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/// Owns the per-listing availability calendar.
///
/// Concurrency model: a UNIQUE(property_id, date) constraint at the database
/// level guarantees that two parallel bookings for overlapping date ranges
/// cannot both succeed — the second insert will fail with a constraint
/// violation, which we translate to a clear domain error. This avoids the
/// classic check-then-insert race that pessimistic or optimistic locking
/// schemes are usually built to fix.
@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyAvailabilityService {

    private final PropertyAvailabilityRepository availabilityRepository;
    private final PropertyRepository propertyRepository;

    @Transactional(readOnly = true)
    public List<PropertyAvailability> getRange(UUID propertyId, LocalDate start, LocalDate end) {
        validateRange(start, end);
        return availabilityRepository.findRange(propertyId, start, end);
    }

    @Transactional(readOnly = true)
    public boolean isAvailable(UUID propertyId, LocalDate start, LocalDate endExclusive) {
        validateRange(start, endExclusive);
        // We store dates as inclusive points; a booking from check-in to
        // check-out occupies [startDate, endDate - 1]. The caller passes
        // endExclusive (= checkout date) so we subtract one day for the
        // inclusive query.
        return availabilityRepository.countConflicts(
                propertyId, start, endExclusive.minusDays(1)) == 0;
    }

    /// Host-imposed block. Idempotent within a request — if a date is already
    /// blocked we silently skip it; if it is BOOKED we refuse the whole
    /// operation rather than partially apply.
    @Transactional
    public void blockRange(UUID propertyId, UUID landlordId,
                           LocalDate start, LocalDate endInclusive) {
        validateRange(start, endInclusive.plusDays(1));
        Property property = requireOwned(propertyId, landlordId);

        var existing = availabilityRepository.findRange(propertyId, start, endInclusive);
        for (var row : existing) {
            if (row.getStatus() == PropertyAvailability.Status.BOOKED) {
                throw new ConflictException(
                        "Cannot block date " + row.getDate()
                                + " — it is reserved by an active booking");
            }
        }
        var existingDates = existing.stream()
                .map(PropertyAvailability::getDate)
                .toList();

        var toInsert = new ArrayList<PropertyAvailability>();
        for (LocalDate d = start; !d.isAfter(endInclusive); d = d.plusDays(1)) {
            if (existingDates.contains(d)) continue;
            var row = new PropertyAvailability();
            row.setPropertyId(propertyId);
            row.setDate(d);
            row.setStatus(PropertyAvailability.Status.BLOCKED);
            toInsert.add(row);
        }
        availabilityRepository.saveAll(toInsert);
        log.info("Host blocked {} dates on property {}", toInsert.size(), propertyId);
    }

    @Transactional
    public void unblockRange(UUID propertyId, UUID landlordId,
                             LocalDate start, LocalDate endInclusive) {
        validateRange(start, endInclusive.plusDays(1));
        requireOwned(propertyId, landlordId);

        var rows = availabilityRepository.findRange(propertyId, start, endInclusive);
        var toDelete = rows.stream()
                .filter(r -> r.getStatus() == PropertyAvailability.Status.BLOCKED)
                .toList();
        availabilityRepository.deleteAll(toDelete);
        log.info("Host unblocked {} dates on property {}", toDelete.size(), propertyId);
    }

    /// Reserves every date in [start, endExclusive). Called from the booking
    /// flow once a booking is approved/paid. Throws if any date in the range
    /// is already taken — the unique constraint catches the race even if two
    /// requests slip past the prior `isAvailable` check.
    @Transactional
    public void reserveForBooking(UUID propertyId, UUID bookingId,
                                  LocalDate start, LocalDate endExclusive) {
        validateRange(start, endExclusive);
        var rows = new ArrayList<PropertyAvailability>();
        for (LocalDate d = start; d.isBefore(endExclusive); d = d.plusDays(1)) {
            var row = new PropertyAvailability();
            row.setPropertyId(propertyId);
            row.setDate(d);
            row.setStatus(PropertyAvailability.Status.BOOKED);
            row.setBookingId(bookingId);
            rows.add(row);
        }
        try {
            availabilityRepository.saveAll(rows);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    "Selected dates are no longer available — please pick another range");
        }
    }

    /// Releases every date held by a booking. Used on cancellation / refund.
    @Transactional
    public void releaseForBooking(UUID bookingId) {
        int deleted = availabilityRepository.deleteByBookingId(bookingId);
        log.info("Released {} dates for cancelled booking {}", deleted, bookingId);
    }

    private Property requireOwned(UUID propertyId, UUID landlordId) {
        var property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new UnauthorizedException(
                    "Only the listing owner can edit its availability");
        }
        return property;
    }

    private void validateRange(LocalDate start, LocalDate endExclusive) {
        if (start == null || endExclusive == null) {
            throw new ConflictException("Date range is required");
        }
        if (!endExclusive.isAfter(start)) {
            throw new ConflictException("End date must be after start date");
        }
    }
}
