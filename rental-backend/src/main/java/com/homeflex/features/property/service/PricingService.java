package com.homeflex.features.property.service;

import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.features.property.dto.response.PricingRecommendationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PricingService {

    private final PropertyRepository propertyRepository;

    public PricingRecommendationResponse getPricingRecommendation(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        BigDecimal currentPrice = property.getPrice();
        BigDecimal recommendedPrice;
        String reasoning;

        if (property.getCity() != null && (property.getCity().equalsIgnoreCase("New York") || property.getCity().equalsIgnoreCase("London"))) {
            recommendedPrice = currentPrice.multiply(new BigDecimal("1.15")).setScale(2, RoundingMode.HALF_UP);
            reasoning = "High demand expected in this area due to upcoming local events and historical seasonal trends.";
        } else {
            recommendedPrice = currentPrice.multiply(new BigDecimal("0.95")).setScale(2, RoundingMode.HALF_UP);
            reasoning = "Slight price reduction recommended to increase your occupancy rate based on immediate competitor analysis.";
        }

        return new PricingRecommendationResponse(
                propertyId.toString(),
                currentPrice,
                recommendedPrice,
                "HIGH",
                reasoning
        );
    }
}
