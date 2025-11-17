package com.realestate.rental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// PropertyCreateRequest.java
@Data
public class PropertyCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String propertyType;

    @NotBlank
    private String listingType;

    @NotNull
    private BigDecimal price;

    private String currency = "XAF";

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    private String stateProvince;

    @NotBlank
    private String country;

    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer bedrooms;
    private Integer bathrooms;
    private BigDecimal areaSqm;
    private Integer floorNumber;
    private Integer totalFloors;
    private LocalDate availableFrom;
    private List<UUID> amenityIds;
}
