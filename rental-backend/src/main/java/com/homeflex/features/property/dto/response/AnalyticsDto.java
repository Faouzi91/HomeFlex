package com.homeflex.features.property.dto.response;

import java.util.List;
import java.util.Map;

public record AnalyticsDto(
        Long totalUsers,
        Long totalTenants,
        Long totalLandlords,
        Long totalProperties,
        Long pendingProperties,
        Long approvedProperties,
        Long totalBookings,
        Long pendingBookings,
        Long approvedBookings,
        Long totalMessages,
        Map<String, Long> propertiesByType,
        Map<String, Long> propertiesByCity,
        Map<String, Long> bookingsByStatus,
        List<TopPropertyDto> topViewedProperties,
        List<TopPropertyDto> topFavoritedProperties
) {}