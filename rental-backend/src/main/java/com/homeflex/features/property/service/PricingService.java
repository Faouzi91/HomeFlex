package com.homeflex.features.property.service;

import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.PricingRule;
import com.homeflex.features.property.domain.repository.PricingRuleRepository;
import com.homeflex.features.property.domain.repository.PropertyRepository;
import com.homeflex.features.property.dto.request.PricingRuleCreateRequest;
import com.homeflex.features.property.dto.response.PricingRecommendationResponse;
import com.homeflex.features.property.dto.response.PricingRuleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PricingService {

    private final PropertyRepository propertyRepository;
    private final PricingRuleRepository pricingRuleRepository;

    // ── Rule CRUD ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PricingRuleDto> getRules(UUID propertyId) {
        return pricingRuleRepository.findByPropertyId(propertyId)
                .stream().map(this::toDto).toList();
    }

    public PricingRuleDto createRule(UUID propertyId, PricingRuleCreateRequest req) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        PricingRule rule = new PricingRule();
        rule.setProperty(property);
        rule.setRuleType(req.ruleType().toUpperCase());
        rule.setLabel(req.label());
        rule.setMultiplier(req.multiplier());
        rule.setMinStayDays(req.minStayDays());
        rule.setStartDate(req.startDate());
        rule.setEndDate(req.endDate());

        return toDto(pricingRuleRepository.save(rule));
    }

    public void deleteRule(UUID propertyId, UUID ruleId) {
        pricingRuleRepository.deleteByPropertyIdAndId(propertyId, ruleId);
    }

    // ── Price Calculation ─────────────────────────────────────────────────────

    /**
     * Calculates the base price for a stay by applying all active pricing rules
     * day-by-day. Priority: SEASONAL overrides WEEKEND; LONG_STAY is a whole-stay
     * discount applied first, then per-day modifiers stack on top.
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateBasePrice(Property property, LocalDate startDate, LocalDate endDate) {
        List<PricingRule> rules = pricingRuleRepository.findByPropertyId(property.getId());
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal baseNightly = property.getPrice();

        // Long-stay discount: applies when total nights >= minStayDays
        BigDecimal longStayMultiplier = rules.stream()
                .filter(r -> "LONG_STAY".equals(r.getRuleType())
                        && r.getMinStayDays() != null
                        && days >= r.getMinStayDays())
                .min(Comparator.comparing(PricingRule::getMultiplier)) // best deal wins
                .map(PricingRule::getMultiplier)
                .orElse(BigDecimal.ONE);

        BigDecimal effectiveNightly = baseNightly.multiply(longStayMultiplier);

        // Sum day-by-day, applying weekend and seasonal modifiers
        BigDecimal total = BigDecimal.ZERO;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            BigDecimal dayPrice = effectiveNightly;
            final LocalDate d = date;

            // Seasonal rules take priority over weekend
            BigDecimal seasonalMult = rules.stream()
                    .filter(r -> "SEASONAL".equals(r.getRuleType())
                            && r.getStartDate() != null && r.getEndDate() != null
                            && !d.isBefore(r.getStartDate()) && !d.isAfter(r.getEndDate()))
                    .map(PricingRule::getMultiplier)
                    .findFirst()
                    .orElse(null);

            if (seasonalMult != null) {
                dayPrice = dayPrice.multiply(seasonalMult);
            } else if (date.getDayOfWeek() == DayOfWeek.SATURDAY
                    || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                BigDecimal weekendMult = rules.stream()
                        .filter(r -> "WEEKEND".equals(r.getRuleType()))
                        .map(PricingRule::getMultiplier)
                        .findFirst()
                        .orElse(BigDecimal.ONE);
                dayPrice = dayPrice.multiply(weekendMult);
            }

            total = total.add(dayPrice);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    // ── Recommendation (legacy) ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PricingRecommendationResponse getPricingRecommendation(UUID propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        BigDecimal current = property.getPrice();
        // Simple market-based suggestion; replace with ML model when data exists
        BigDecimal recommended = current.multiply(new BigDecimal("1.08")).setScale(2, RoundingMode.HALF_UP);

        return new PricingRecommendationResponse(
                propertyId.toString(),
                current,
                recommended,
                "MEDIUM",
                "Based on current occupancy trends, a small price increase may improve revenue without reducing bookings."
        );
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private PricingRuleDto toDto(PricingRule r) {
        return new PricingRuleDto(
                r.getId(), r.getProperty().getId(),
                r.getRuleType(), r.getLabel(), r.getMultiplier(),
                r.getMinStayDays(), r.getStartDate(), r.getEndDate()
        );
    }
}
