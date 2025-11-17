package com.realestate.rental.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

// PropertySearchParams.java
@Data
@Builder
public class PropertySearchParams {
    private String city;
    private Double minPrice;
    private Double maxPrice;
    private String propertyType;
    private Integer bedrooms;
    private Integer bathrooms;
    private List<String> amenities;
}
