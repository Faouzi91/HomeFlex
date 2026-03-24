package com.realestate.rental.service;

import com.realestate.rental.application.mapper.PropertyMapper;
import com.realestate.rental.application.mapper.UserMapper;
import com.realestate.rental.dto.*;
import com.realestate.rental.utils.entity.*;
import com.realestate.rental.repository.*;
import com.realestate.rental.utils.entity.Property;
import com.realestate.rental.utils.enumeration.BookingStatus;
import com.realestate.rental.utils.enumeration.PropertyStatus;
import com.realestate.rental.utils.enumeration.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final MessageRepository messageRepository;
    private final ReportedListingRepository reportedListingRepository;
    private final NotificationServiceExtension notificationService;
    private final PropertyMapper propertyMapper;
    private final UserMapper userMapper;

    public Page<PropertyDto> getPendingProperties(Pageable pageable) {
        return propertyRepository.findByStatus(PropertyStatus.PENDING, pageable)
                .map(this::mapToPropertyDto);
    }

    public PropertyDto approveProperty(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setStatus(PropertyStatus.APPROVED);
        property = propertyRepository.save(property);

        // Notify landlord
        notificationService.sendPropertyApprovedNotification(property.getLandlord(), property);

        return mapToPropertyDto(property);
    }

    public PropertyDto rejectProperty(UUID propertyId, String reason) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setStatus(PropertyStatus.REJECTED);
        property = propertyRepository.save(property);

        // Notify landlord with reason
        notificationService.sendPropertyRejectedNotification(property.getLandlord(), property, reason);

        return mapToPropertyDto(property);
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToUserDto);
    }

    public UserDto suspendUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(false);
        user = userRepository.save(user);

        return mapToUserDto(user);
    }

    public UserDto activateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(true);
        user = userRepository.save(user);

        return mapToUserDto(user);
    }

    // helper to convert List<Object[]> to Map<String, Long>
    private Map<String, Long> toMapFromObjectArray(List<Object[]> rows) {
        if (rows == null) return Collections.emptyMap();
        return rows.stream()
                .filter(r -> r != null && r.length >= 2)
                .collect(Collectors.toMap(
                        r -> String.valueOf(r[0]),                      // group key as string
                        r -> ((Number) r[1]).longValue()               // count as long
                ));
    }

    public AnalyticsDto getAnalytics() {
        // User statistics
        long totalUsers = userRepository.count();
        long totalTenants = userRepository.countByRole(UserRole.TENANT);
        long totalLandlords = userRepository.countByRole(UserRole.LANDLORD);

        // Property statistics
        long totalProperties = propertyRepository.count();
        long pendingProperties = propertyRepository.countByStatus(PropertyStatus.PENDING);
        long approvedProperties = propertyRepository.countByStatus(PropertyStatus.APPROVED);

        // Booking statistics
        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
        long approvedBookings = bookingRepository.countByStatus(BookingStatus.APPROVED);

        // Message statistics
        long totalMessages = messageRepository.count();

        // Properties by type
        // Properties by type
        List<Object[]> propsByTypeRows = propertyRepository.countByPropertyType();
        Map<String, Long> propertiesByType = toMapFromObjectArray(propsByTypeRows);

        // Properties by city
        List<Object[]> propsByCityRows = propertyRepository.countByCity();
        Map<String, Long> propertiesByCity = toMapFromObjectArray(propsByCityRows);

        // Bookings by status
        List<Object[]> bookingsByStatusRows = bookingRepository.countByStatusGrouped();
        Map<String, Long> bookingsByStatus = toMapFromObjectArray(bookingsByStatusRows);


        // Top viewed properties
        List<TopPropertyDto> topViewedProperties = propertyRepository
                .findTop10ByOrderByViewCountDesc().stream()
                .map(this::mapToTopPropertyDto)
                .collect(Collectors.toList());

        // Top favorited properties
        List<TopPropertyDto> topFavoritedProperties = propertyRepository
                .findTop10ByOrderByFavoriteCountDesc().stream()
                .map(this::mapToTopPropertyDto)
                .collect(Collectors.toList());

        return new AnalyticsDto(
                totalUsers,
                totalTenants,
                totalLandlords,
                totalProperties,
                pendingProperties,
                approvedProperties,
                totalBookings,
                pendingBookings,
                approvedBookings,
                totalMessages,
                propertiesByType,
                propertiesByCity,
                bookingsByStatus,
                topViewedProperties,
                topFavoritedProperties
        );
    }

    public Page<ReportDto> getReports(Pageable pageable) {
        return reportedListingRepository.findByStatus("PENDING", pageable)
                .map(this::mapToReportDto);
    }

    public ReportDto resolveReport(UUID reportId) {
        ReportedListing report = reportedListingRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        report.setStatus("RESOLVED");
        report.setResolvedAt(LocalDateTime.now());
        report = reportedListingRepository.save(report);

        return mapToReportDto(report);
    }

    public ReportDto createReport(UUID propertyId, UUID reporterId, com.realestate.rental.dto.request.ReportListingRequest request) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));

        ReportedListing report = new ReportedListing();
        report.setProperty(property);
        report.setReporter(reporter);
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        report.setStatus("PENDING");

        report = reportedListingRepository.save(report);

        return mapToReportDto(report);
    }

    public List<ReportDto> getReportsByProperty(UUID propertyId) {
        return reportedListingRepository.findByPropertyId(propertyId)
                .stream()
                .map(this::mapToReportDto)
                .collect(Collectors.toList());
    }

    // Mapping methods
    private PropertyDto mapToPropertyDto(Property property) {
        return propertyMapper.toDto(property);
    }

    private UserDto mapToUserDto(User user) {
        return userMapper.toDto(user);
    }

    private TopPropertyDto mapToTopPropertyDto(Property property) {
        return new TopPropertyDto(
                property.getId(),
                property.getTitle(),
                property.getCity(),
                property.getViewCount(),
                property.getFavoriteCount()
        );
    }

    private ReportDto mapToReportDto(ReportedListing report) {
        return new ReportDto(
                report.getId(),
                report.getProperty().getId(),
                report.getProperty().getTitle(),
                mapToUserDto(report.getReporter()),
                report.getReason(),
                report.getDescription(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getResolvedAt(),
                report.getResolvedBy() != null ? mapToUserDto(report.getResolvedBy()) : null
        );
    }
}
