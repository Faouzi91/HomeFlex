package com.realestate.rental.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

// PropertyUpdateRequest.java
@Data
public class PropertyUpdateRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private Boolean isAvailable;
    private LocalDate availableFrom;
    // Other optional fields...
}
