package com.homeflex.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GdprService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Map<String, Object> exportUserData(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Map<String, Object> exportData = new HashMap<>();
        
        // Basic Profile Info
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId().toString());
        profile.put("email", user.getEmail());
        profile.put("firstName", user.getFirstName());
        profile.put("lastName", user.getLastName());
        profile.put("phoneNumber", user.getPhoneNumber());
        profile.put("role", user.getRole().name());
        profile.put("createdAt", user.getCreatedAt().toString());
        profile.put("languagePreference", user.getLanguagePreference());
        profile.put("trustScore", user.getTrustScore());
        
        exportData.put("profile", profile);
        
        // In a real scenario, this would aggregate data from all other domains:
        // - Bookings
        // - Properties
        // - Reviews
        // - Maintenance Requests
        // - Payment History
        exportData.put("bookings", "Data portability for bookings is being aggregated...");
        exportData.put("reviews", "Data portability for reviews is being aggregated...");

        log.info("GDPR data export generated for user {}", userId);
        
        return exportData;
    }

    @Transactional
    public void eraseUserData(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Right to be forgotten (Erasure)
        // Hard deletion or anonymization depending on compliance requirements (e.g., retaining financial records).
        // Here we do a soft-delete + PII anonymization to maintain referential integrity.
        
        user.setEmail("deleted-" + UUID.randomUUID() + "@homeflex.com");
        user.setFirstName("Deleted");
        user.setLastName("User");
        user.setPhoneNumber(null);
        user.setProfilePictureUrl(null);
        user.setIsActive(false);
        user.setPasswordHash("DELETED");
        
        userRepository.save(user);
        
        log.info("GDPR data erasure completed for user {}", userId);
    }
}
