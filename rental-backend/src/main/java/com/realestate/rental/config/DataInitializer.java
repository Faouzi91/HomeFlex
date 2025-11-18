package com.realestate.rental.config;

import com.realestate.rental.repository.UserRepository;
import com.realestate.rental.utils.entity.User;
import com.realestate.rental.utils.enumeration.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@realestate.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Value("${app.admin.first-name:Admin}")
    private String adminFirstName;

    @Value("${app.admin.last-name:User}")
    private String adminLastName;

    @Override
    public void run(String... args) {
        createDefaultAdmin();
        createTestUsers();
    }

    private void createDefaultAdmin() {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("✅ Admin user already exists: {}", adminEmail);
            return;
        }

        try {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setFirstName(adminFirstName);
            admin.setLastName(adminLastName);
            admin.setRole(UserRole.ADMIN);
            admin.setIsActive(true);
            admin.setIsVerified(true);
            admin.setLanguagePreference("en");

            userRepository.save(admin);

            log.info("═══════════════════════════════════════════════════════");
            log.info("✅ DEFAULT ADMIN USER CREATED SUCCESSFULLY!");
            log.info("═══════════════════════════════════════════════════════");
            log.info("📧 Email: {}", adminEmail);
            log.info("🔑 Password: {}", adminPassword);
            log.info("👤 Role: ADMIN");
            log.info("═══════════════════════════════════════════════════════");
            log.info("⚠️  IMPORTANT: Change this password in production!");
            log.info("═══════════════════════════════════════════════════════");

        } catch (Exception e) {
            log.error("❌ Failed to create default admin user: {}", e.getMessage());
        }
    }

    private void createTestUsers() {
        // Create test landlord
        if (!userRepository.existsByEmail("landlord@test.com")) {
            try {
                User landlord = new User();
                landlord.setEmail("landlord@test.com");
                landlord.setPasswordHash(passwordEncoder.encode("Landlord@123"));
                landlord.setFirstName("John");
                landlord.setLastName("Landlord");
                landlord.setPhoneNumber("+237123456789");
                landlord.setRole(UserRole.LANDLORD);
                landlord.setIsActive(true);
                landlord.setIsVerified(true);
                landlord.setLanguagePreference("en");

                userRepository.save(landlord);
                log.info("✅ Test Landlord created: landlord@test.com / Landlord@123");
            } catch (Exception e) {
                log.warn("⚠️  Could not create test landlord: {}", e.getMessage());
            }
        }

        // Create test tenant
        if (!userRepository.existsByEmail("tenant@test.com")) {
            try {
                User tenant = new User();
                tenant.setEmail("tenant@test.com");
                tenant.setPasswordHash(passwordEncoder.encode("Tenant@123"));
                tenant.setFirstName("Jane");
                tenant.setLastName("Tenant");
                tenant.setPhoneNumber("+237987654321");
                tenant.setRole(UserRole.TENANT);
                tenant.setIsActive(true);
                tenant.setIsVerified(true);
                tenant.setLanguagePreference("en");

                userRepository.save(tenant);
                log.info("✅ Test Tenant created: tenant@test.com / Tenant@123");
            } catch (Exception e) {
                log.warn("⚠️  Could not create test tenant: {}", e.getMessage());
            }
        }
    }
}