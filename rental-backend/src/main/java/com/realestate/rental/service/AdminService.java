package com.realestate.rental.service;

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
        Map<String, Long> propertiesByType = propertyRepository.countByPropertyType();

        // Properties by city
        Map<String, Long> propertiesByCity = propertyRepository.countByCity();

        // Bookings by status
        Map<String, Long> bookingsByStatus = bookingRepository.countByStatusGrouped();

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

        return AnalyticsDto.builder()
                .totalUsers(totalUsers)
                .totalTenants(totalTenants)
                .totalLandlords(totalLandlords)
                .totalProperties(totalProperties)
                .pendingProperties(pendingProperties)
                .approvedProperties(approvedProperties)
                .totalBookings(totalBookings)
                .pendingBookings(pendingBookings)
                .approvedBookings(approvedBookings)
                .totalMessages(totalMessages)
                .propertiesByType(propertiesByType)
                .propertiesByCity(propertiesByCity)
                .bookingsByStatus(bookingsByStatus)
                .topViewedProperties(topViewedProperties)
                .topFavoritedProperties(topFavoritedProperties)
                .build();
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

    // Mapping methods
    private PropertyDto mapToPropertyDto(Property property) {
        PropertyDto dto = new PropertyDto();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setPropertyType(property.getPropertyType().name());
        dto.setPrice(property.getPrice());
        dto.setCity(property.getCity());
        dto.setStatus(property.getStatus().name());
        dto.setCreatedAt(property.getCreatedAt());
        return dto;
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private TopPropertyDto mapToTopPropertyDto(Property property) {
        return TopPropertyDto.builder()
                .id(property.getId())
                .title(property.getTitle())
                .city(property.getCity())
                .viewCount(property.getViewCount())
                .favoriteCount(property.getFavoriteCount())
                .build();
    }

    private ReportDto mapToReportDto(ReportedListing report) {
        return ReportDto.builder()
                .id(report.getId())
                .propertyId(report.getProperty().getId())
                .propertyTitle(report.getProperty().getTitle())
                .reporter(mapToUserDto(report.getReporter()))
                .reason(report.getReason())
                .description(report.getDescription())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .resolvedAt(report.getResolvedAt())
                .resolvedBy(report.getResolvedBy() != null ? mapToUserDto(report.getResolvedBy()) : null)
                .build();
    }
}
