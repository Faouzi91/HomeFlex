package com.realestate.rental.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class AnalyticsDto {
    private Long totalUsers;
    private Long totalTenants;
    private Long totalLandlords;
    private Long totalProperties;
    private Long pendingProperties;
    private Long approvedProperties;
    private Long totalBookings;
    private Long pendingBookings;
    private Long approvedBookings;
    private Long totalMessages;
    private Map<String, Long> propertiesByType;
    private Map<String, Long> propertiesByCity;
    private Map<String, Long> bookingsByStatus;
    private List<TopPropertyDto> topViewedProperties;
    private List<TopPropertyDto> topFavoritedProperties;
}