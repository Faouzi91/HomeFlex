package com.homeflex.features.finance.service;

import com.homeflex.core.domain.entity.User;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.service.StorageService;
import com.homeflex.features.finance.domain.entity.Receipt;
import com.homeflex.features.finance.domain.repository.ReceiptRepository;
import com.homeflex.features.property.domain.entity.Booking;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceService {

    private final ReceiptRepository receiptRepository;
    private final StorageService storageService;

    @Transactional
    public Receipt generateReceipt(Booking booking) {
        User tenant = booking.getTenant();
        
        byte[] pdfBytes = createReceiptPdf(booking, tenant);
        String fileName = "receipt-" + booking.getId().toString() + ".pdf";
        String url = storageService.uploadFile(pdfBytes, fileName, "application/pdf", "receipts");

        Receipt receipt = new Receipt();
        receipt.setBooking(booking);
        receipt.setUser(tenant);
        receipt.setReceiptNumber("HF-REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        receipt.setAmount(booking.getTotalPrice());
        receipt.setCurrency(booking.getProperty().getCurrency());
        receipt.setReceiptUrl(url);

        log.info("Receipt {} generated for booking {}", receipt.getReceiptNumber(), booking.getId());
        
        return receiptRepository.save(receipt);
    }

    private byte[] createReceiptPdf(Booking booking, User tenant) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);

            document.add(new Paragraph("HomeFlex Payment Receipt", titleFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Customer: " + tenant.getFirstName() + " " + tenant.getLastName(), normalFont));
            document.add(new Paragraph("Email: " + tenant.getEmail(), normalFont));
            document.add(new Paragraph("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Booking Reference: " + booking.getId(), normalFont));
            document.add(new Paragraph("Property: " + booking.getProperty().getTitle(), normalFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Amount Paid: " + booking.getTotalPrice() + " " + booking.getProperty().getCurrency(), boldFont));
            document.add(new Paragraph("Status: PAID", normalFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Thank you for choosing HomeFlex!", normalFont));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF receipt for booking: {}", booking.getId(), e);
            throw new DomainException("Failed to generate PDF receipt");
        }
    }

    @Transactional(readOnly = true)
    public List<Receipt> getMyReceipts(UUID userId) {
        return receiptRepository.findByUserId(userId);
    }
}
