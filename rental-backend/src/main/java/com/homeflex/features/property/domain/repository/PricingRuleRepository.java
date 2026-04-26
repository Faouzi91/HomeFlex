package com.homeflex.features.property.domain.repository;

import com.homeflex.features.property.domain.entity.PricingRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PricingRuleRepository extends JpaRepository<PricingRule, UUID> {
    List<PricingRule> findByPropertyId(UUID propertyId);
    void deleteByPropertyIdAndId(UUID propertyId, UUID ruleId);
}
