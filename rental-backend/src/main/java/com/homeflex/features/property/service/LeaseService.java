package com.homeflex.features.property.service;

import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.service.StorageService;
import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.PropertyLease;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.features.property.domain.repository.PropertyLeaseRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaseService {

    private final PropertyLeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Transactional
    public PropertyLease generateLease(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        Property property = booking.getProperty();
        User landlord = property.getLandlord();
        User tenant = booking.getTenant();

        // In a real app, we would use a PDF generation library like iText or OpenPDF
        // For now, we'll simulate it by creating a record with a placeholder URL.
        PropertyLease lease = new PropertyLease();
        lease.setProperty(property);
        lease.setBooking(booking);
        lease.setLandlord(landlord);
        lease.setTenant(tenant);
        lease.setStatus("PENDING");
        lease.setLeaseUrl("https://placeholder.local/leases/" + bookingId + ".pdf");

        return leaseRepository.save(lease);
    }

    @Transactional
    public PropertyLease uploadLeaseTemplate(UUID propertyId, MultipartFile file, UUID landlordId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new DomainException("Only the property landlord can upload lease templates");
        }

        String url = storageService.uploadFile(file, "leases/templates");

        PropertyLease lease = new PropertyLease();
        lease.setProperty(property);
        lease.setLandlord(property.getLandlord());
        lease.setLeaseUrl(url);
        lease.setStatus("TEMPLATE");

        return leaseRepository.save(lease);
    }

    @Transactional
    public PropertyLease signLease(UUID leaseId, UUID userId) {
        PropertyLease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Lease not found"));

        if (!lease.getTenant().getId().equals(userId)) {
            throw new DomainException("Only the tenant can sign this lease");
        }

        if (!"PENDING".equals(lease.getStatus())) {
            throw new DomainException("Lease is not in a signable state: " + lease.getStatus());
        }

        lease.setStatus("SIGNED");
        lease.setSignedAt(LocalDateTime.now());

        return leaseRepository.save(lease);
    }

    @Transactional(readOnly = true)
    public List<PropertyLease> getMyLeases(UUID userId) {
        // Returns leases where user is either landlord or tenant
        List<PropertyLease> asLandlord = leaseRepository.findByLandlordId(userId);
        List<PropertyLease> asTenant = leaseRepository.findByTenantId(userId);
        asLandlord.addAll(asTenant);
        return asLandlord;
    }

    @Transactional(readOnly = true)
    public PropertyLease getLeaseByBooking(UUID bookingId) {
        return leaseRepository.findByBookingId(bookingId).stream()
                .filter(l -> !"TEMPLATE".equals(l.getStatus()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Lease not found for booking: " + bookingId));
    }
}
