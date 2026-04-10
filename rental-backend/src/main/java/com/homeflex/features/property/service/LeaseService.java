package com.homeflex.features.property.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

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

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        byte[] pdfBytes = createPdfDocument(property, landlord, tenant, booking);
        String fileName = "lease-" + bookingId.toString() + ".pdf";
        String url = storageService.uploadFile(pdfBytes, fileName, "application/pdf", "leases/generated");

        PropertyLease lease = new PropertyLease();
        lease.setProperty(property);
        lease.setBooking(booking);
        lease.setLandlord(landlord);
        lease.setTenant(tenant);
        lease.setStatus("PENDING");
        lease.setLeaseUrl(url);

        return leaseRepository.save(lease);
    }

    private byte[] createPdfDocument(Property property, User landlord, User tenant, Booking booking) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

            document.add(new Paragraph("HomeFlex Rental Agreement", titleFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Property Details", headerFont));
            document.add(new Paragraph("Title: " + property.getTitle(), normalFont));
            document.add(new Paragraph("Address: " + property.getAddress() + ", " + property.getCity() + ", " + property.getCountry(), normalFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Landlord", headerFont));
            document.add(new Paragraph("Name: " + landlord.getFirstName() + " " + landlord.getLastName(), normalFont));
            document.add(new Paragraph("Email: " + landlord.getEmail(), normalFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Tenant", headerFont));
            document.add(new Paragraph("Name: " + tenant.getFirstName() + " " + tenant.getLastName(), normalFont));
            document.add(new Paragraph("Email: " + tenant.getEmail(), normalFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Lease Terms", headerFont));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            document.add(new Paragraph("Start Date: " + booking.getStartDate().format(formatter), normalFont));
            document.add(new Paragraph("End Date: " + booking.getEndDate().format(formatter), normalFont));
            document.add(new Paragraph("Total Price: " + booking.getTotalPrice() + " " + property.getCurrency(), normalFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("This is an electronically generated lease agreement via HomeFlex.", normalFont));
            document.add(new Paragraph("Generated at: " + LocalDateTime.now(), normalFont));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF lease for booking: {}", booking.getId(), e);
            throw new DomainException("Failed to generate PDF lease document");
        }
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
