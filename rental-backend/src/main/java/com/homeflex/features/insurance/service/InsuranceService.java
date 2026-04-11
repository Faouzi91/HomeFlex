package com.homeflex.features.insurance.service;

import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.features.insurance.domain.entity.InsurancePlan;
import com.homeflex.features.insurance.domain.entity.InsurancePolicy;
import com.homeflex.features.insurance.domain.repository.InsurancePlanRepository;
import com.homeflex.features.insurance.domain.repository.InsurancePolicyRepository;
import com.homeflex.features.insurance.dto.response.InsurancePlanResponse;
import com.homeflex.features.property.domain.entity.Booking;
import com.homeflex.features.property.domain.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsurancePlanRepository planRepository;
    private final InsurancePolicyRepository policyRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public List<InsurancePlanResponse> getAvailablePlans(String type) {
        return planRepository.findByType(type).stream()
                .map(plan -> new InsurancePlanResponse(
                        plan.getId(),
                        plan.getProvider().getName(),
                        plan.getName(),
                        plan.getType(),
                        plan.getDescription(),
                        plan.getCoverageDetails(),
                        plan.getDailyPremium(),
                        plan.getMaxCoverageAmount()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public InsurancePolicy purchasePolicy(UUID userId, UUID planId, UUID bookingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        InsurancePlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Insurance plan not found"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        long days = ChronoUnit.DAYS.between(booking.getStartDate(), booking.getEndDate());
        if (days <= 0) days = 1;

        BigDecimal totalPremium = plan.getDailyPremium().multiply(new BigDecimal(days));

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPlan(plan);
        policy.setUser(user);
        policy.setBooking(booking);
        policy.setPolicyNumber("HF-INS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        policy.setStartDate(booking.getStartDate());
        policy.setEndDate(booking.getEndDate());
        policy.setTotalPremium(totalPremium);
        policy.setStatus("ACTIVE");
        
        // Mock certificate URL
        policy.setCertificateUrl("https://assets.homeflex.com/certificates/" + policy.getPolicyNumber() + ".pdf");

        log.info("Insurance policy {} purchased for user {} and booking {}", policy.getPolicyNumber(), userId, bookingId);
        
        return policyRepository.save(policy);
    }
}
