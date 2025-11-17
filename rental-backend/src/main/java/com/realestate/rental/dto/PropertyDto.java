package com.realestate.rental.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// PropertyDto.java
@Data
public class PropertyDto {
    private UUID id;
    private String title;
    private String description;
    private String propertyType;
    private String listingType;
    private BigDecimal price;
    private String currency;
    private String address;
    private String city;
    private String stateProvince;
    private String country;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer bedrooms;
    private Integer bathrooms;
    private BigDecimal areaSqm;
    private Integer floorNumber;
    private Integer totalFloors;
    private Boolean isAvailable;
    private LocalDate availableFrom;
    private String status;
    private Integer viewCount;
    private Integer favoriteCount;
    private List<PropertyImageDto> images;
    private List<PropertyVideoDto> videos;
    private List<AmenityDto> amenities;
    private UserDto landlord;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
