package com.realestate.rental.service;

import com.realestate.rental.mapper.AdminMapper;
import com.realestate.rental.mapper.PropertyMapper;
import com.realestate.rental.mapper.ReportMapper;
import com.realestate.rental.mapper.UserMapper;
import com.realestate.rental.dto.response.*;
import com.realestate.rental.domain.repository.*;
import com.realestate.rental.exception.ResourceNotFoundException;
import com.realestate.rental.domain.entity.*;
import com.realestate.rental.domain.entity.Property;
import com.realestate.rental.domain.enums.BookingStatus;
import com.realestate.rental.domain.enums.PropertyStatus;
import com.realestate.rental.domain.enums.UserRole;
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
    private final NotificationService notificationService;
    private final PropertyMapper propertyMapper;
    private final UserMapper userMapper;
    private final ReportMapper reportMapper;
    private final AdminMapper adminMapper;

    public Page<PropertyDto> getPendingProperties(Pageable pageable) {
        return propertyRepository.findByStatus(PropertyStatus.PENDING, pageable)
                .map(propertyMapper::toDto);
    }

    public PropertyDto approveProperty(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        property.setStatus(PropertyStatus.APPROVED);
        property = propertyRepository.save(property);

        // Notify landlord
        notificationService.sendPropertyApprovedNotification(property.getLandlord(), property);

        return propertyMapper.toDto(property);
    }

    public PropertyDto rejectProperty(UUID propertyId, String reason) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        property.setStatus(PropertyStatus.REJECTED);
        property = propertyRepository.save(property);

        // Notify landlord with reason
        notificationService.sendPropertyRejectedNotification(property.getLandlord(), property, reason);

        return propertyMapper.toDto(property);
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    public UserDto suspendUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setIsActive(false);
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto activateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setIsActive(true);
        user = userRepository.save(user);

        return userMapper.toDto(user);
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
        List<TopPropertyDto> topViewedProperties = adminMapper.toTopPropertyDtoList(
                propertyRepository.findTop10ByOrderByViewCountDesc());

        // Top favorited properties
        List<TopPropertyDto> topFavoritedProperties = adminMapper.toTopPropertyDtoList(
                propertyRepository.findTop10ByOrderByFavoriteCountDesc());

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
                .map(reportMapper::toDto);
    }

    public ReportDto resolveReport(UUID reportId) {
        ReportedListing report = reportedListingRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        report.setStatus("RESOLVED");
        report.setResolvedAt(LocalDateTime.now());
        report = reportedListingRepository.save(report);

        return reportMapper.toDto(report);
    }

    public ReportDto createReport(UUID propertyId, UUID reporterId, com.realestate.rental.dto.request.ReportListingRequest request) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResourceNotFoundException("Reporter not found"));

        ReportedListing report = new ReportedListing();
        report.setProperty(property);
        report.setReporter(reporter);
        report.setReason(request.reason());
        report.setDescription(request.description());
        report.setStatus("PENDING");

        report = reportedListingRepository.save(report);

        return reportMapper.toDto(report);
    }

    public List<ReportDto> getReportsByProperty(UUID propertyId) {
        return reportedListingRepository.findByPropertyId(propertyId)
                .stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());
    }
}
