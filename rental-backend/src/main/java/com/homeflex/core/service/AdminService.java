package com.homeflex.core.service;

import com.homeflex.features.property.mapper.AdminMapper;
import com.homeflex.features.property.mapper.PropertyMapper;
import com.homeflex.features.property.mapper.ReportMapper;
import com.homeflex.core.mapper.UserMapper;
import com.homeflex.features.property.dto.response.AnalyticsDto;
import com.homeflex.features.property.dto.response.PropertyDto;
import com.homeflex.features.property.dto.response.ReportDto;
import com.homeflex.features.property.dto.response.TopPropertyDto;
import com.homeflex.core.dto.response.UserDto;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.features.property.domain.repository.BookingRepository;
import com.homeflex.core.domain.repository.MessageRepository;
import com.homeflex.features.property.domain.repository.ReportedListingRepository;
import com.homeflex.core.domain.repository.SystemConfigRepository;
import com.homeflex.features.property.domain.repository.AmenityRepository;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.entity.SystemConfig;
import com.homeflex.features.property.domain.entity.Amenity;
import com.homeflex.features.property.domain.entity.ReportedListing;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.enums.BookingStatus;
import com.homeflex.features.property.domain.enums.PropertyStatus;
import com.homeflex.features.property.domain.repository.RoomTypeRepository;
import com.homeflex.core.domain.enums.UserRole;
import com.homeflex.core.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.ArrayList;
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
    private final SystemConfigRepository systemConfigRepository;
    private final AmenityRepository amenityRepository;
    private final NotificationService notificationService;
    private final PropertyMapper propertyMapper;
    private final UserMapper userMapper;
    private final ReportMapper reportMapper;
    private final AdminMapper adminMapper;
    private final EventOutboxService eventOutboxService;
    private final RoomTypeRepository roomTypeRepository;

    public Page<PropertyDto> getPendingProperties(Pageable pageable) {
        return propertyRepository.findByStatus(PropertyStatus.PENDING, pageable)
                .map(propertyMapper::toDto);
    }

    public PropertyDto approveProperty(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        // Hotel-type properties must have at least one active room type
        if (property.getPropertyType().isHotelType()) {
            long activeRooms = roomTypeRepository.countByPropertyIdAndIsActiveTrue(propertyId);
            if (activeRooms == 0) {
                throw new DomainException(
                        "Cannot approve: hotel-type properties must have at least one active room type configured.");
            }
        }

        // Completeness gate
        List<String> missing = new ArrayList<>();
        if (property.getTitle() == null || property.getTitle().length() < 10) missing.add("title (min 10 chars)");
        if (property.getDescription() == null || property.getDescription().length() < 50) missing.add("description (min 50 chars)");
        if (property.getImages() == null || property.getImages().isEmpty()) missing.add("at least 1 photo");
        if (!missing.isEmpty()) {
            throw new DomainException("Property is incomplete, cannot approve: " + String.join(", ", missing));
        }

        property.setStatus(PropertyStatus.APPROVED);
        property.setApprovedAt(LocalDateTime.now());
        property.setRejectionReason(null);
        property = propertyRepository.save(property);

        eventOutboxService.enqueue("Property", property.getId(), "PropertyIndexed", java.util.Map.of("action", "approved"));
        notificationService.sendPropertyApprovedNotification(property.getLandlord(), property);

        return propertyMapper.toDto(property);
    }

    public PropertyDto rejectProperty(UUID propertyId, String reason) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        property.setStatus(PropertyStatus.REJECTED);
        property.setRejectionReason(reason);
        property = propertyRepository.save(property);

        notificationService.sendPropertyRejectedNotification(property.getLandlord(), property, reason);

        return propertyMapper.toDto(property);
    }

    public PropertyDto suspendProperty(UUID propertyId, String reason) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        property.setStatus(PropertyStatus.SUSPENDED);
        property.setRejectionReason(reason);
        property = propertyRepository.save(property);

        // Remove from search index
        eventOutboxService.enqueue("Property", property.getId(), "PropertyIndexed", java.util.Map.of("action", "deleted"));
        notificationService.sendPropertyRejectedNotification(property.getLandlord(), property,
                "Your property has been suspended: " + reason);

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

    private Map<String, Long> toMapFromObjectArray(List<Object[]> rows) {
        if (rows == null) return Collections.emptyMap();
        return rows.stream()
                .filter(r -> r != null && r.length >= 2)
                .collect(Collectors.toMap(
                        r -> String.valueOf(r[0]),
                        r -> ((Number) r[1]).longValue()
                ));
    }

    public AnalyticsDto getAnalytics() {
        long totalUsers = userRepository.count();
        long totalTenants = userRepository.countByRole(UserRole.TENANT);
        long totalLandlords = userRepository.countByRole(UserRole.LANDLORD);

        long totalProperties = propertyRepository.count();
        long pendingProperties = propertyRepository.countByStatus(PropertyStatus.PENDING);
        long approvedProperties = propertyRepository.countByStatus(PropertyStatus.APPROVED);

        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING_APPROVAL);
        long approvedBookings = bookingRepository.countByStatus(BookingStatus.APPROVED);

        long totalMessages = messageRepository.count();

        List<Object[]> propsByTypeRows = propertyRepository.countByPropertyType();
        Map<String, Long> propertiesByType = toMapFromObjectArray(propsByTypeRows);

        List<Object[]> propsByCityRows = propertyRepository.countByCity();
        Map<String, Long> propertiesByCity = toMapFromObjectArray(propsByCityRows);

        List<Object[]> bookingsByStatusRows = bookingRepository.countByStatusGrouped();
        Map<String, Long> bookingsByStatus = toMapFromObjectArray(bookingsByStatusRows);

        List<TopPropertyDto> topViewedProperties = adminMapper.toTopPropertyDtoList(
                propertyRepository.findTop10ByOrderByViewCountDesc());

        List<TopPropertyDto> topFavoritedProperties = adminMapper.toTopPropertyDtoList(
                propertyRepository.findTop10ByOrderByFavoriteCountDesc());

        return new AnalyticsDto(
                totalUsers, totalTenants, totalLandlords,
                totalProperties, pendingProperties, approvedProperties,
                totalBookings, pendingBookings, approvedBookings,
                totalMessages, propertiesByType, propertiesByCity, bookingsByStatus,
                topViewedProperties, topFavoritedProperties
        );
    }

    public Page<ReportDto> getReports(Pageable pageable) {
        return reportedListingRepository.findByStatus("PENDING", pageable)
                .map(reportMapper::toDto);
    }

    public ReportDto resolveReport(UUID reportId, UUID adminId, String notes) {
        ReportedListing report = reportedListingRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        report.setStatus("RESOLVED");
        report.setResolvedBy(admin);
        report.setResolutionNotes(notes);
        report.setResolvedAt(LocalDateTime.now());
        report = reportedListingRepository.save(report);

        notificationService.sendReportResolvedNotification(report.getReporter(), report.getProperty(), notes);

        return reportMapper.toDto(report);
    }

    public ReportDto createReport(UUID propertyId, UUID reporterId, com.homeflex.features.property.dto.request.ReportListingRequest request) {
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

    public List<SystemConfig> getAllConfigs() {
        return systemConfigRepository.findAll();
    }

    public SystemConfig updateConfig(String key, String value) {
        SystemConfig config = systemConfigRepository.findByConfigKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Config not found: " + key));
        config.setConfigValue(value);
        return systemConfigRepository.save(config);
    }

    public List<Amenity> listAmenities() {
        return amenityRepository.findAll(org.springframework.data.domain.Sort.by("category", "name"));
    }

    public Amenity createAmenity(Amenity amenity) {
        return amenityRepository.save(amenity);
    }

    public Amenity updateAmenity(UUID id, Amenity patch) {
        Amenity existing = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found"));
        if (patch.getName() != null) existing.setName(patch.getName());
        if (patch.getNameFr() != null) existing.setNameFr(patch.getNameFr());
        if (patch.getIcon() != null) existing.setIcon(patch.getIcon());
        if (patch.getCategory() != null) existing.setCategory(patch.getCategory());
        return amenityRepository.save(existing);
    }

    public void deleteAmenity(UUID id) {
        amenityRepository.deleteById(id);
    }
}
