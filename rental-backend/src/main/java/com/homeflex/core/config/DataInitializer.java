package com.homeflex.core.config;

import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.domain.repository.RoleRepository;
import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
@Profile("!prod")  // Never create test data in production
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@homeflex.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Value("${app.admin.first-name:Platform}")
    private String adminFirstName;

    @Value("${app.admin.last-name:Admin}")
    private String adminLastName;

    @Override
    public void run(String... args) {
        createDefaultAdmin();
        createTestUsers();
    }

    private void createDefaultAdmin() {
        userRepository.findByEmail(adminEmail).ifPresentOrElse(
            existing -> {
                log.info(" Admin user already exists: {}", adminEmail);
                ensureRbacRole(existing, "ROLE_ADMIN");
            },
            () -> {
                try {
                    User admin = new User();
                    admin.setEmail(adminEmail);
                    admin.setPasswordHash(passwordEncoder.encode(adminPassword));
                    admin.setFirstName(adminFirstName);
                    admin.setLastName(adminLastName);
                    admin.setRole(UserRole.ADMIN);
                    assignRbacRole(admin, "ROLE_ADMIN");
                    admin.setIsActive(true);
                    admin.setIsVerified(true);
                    admin.setLanguagePreference("en");
                    userRepository.save(admin);
                    log.info("═══════════════════════════════════════════════════════");
                    log.info(" DEFAULT ADMIN USER CREATED: {}", adminEmail);
                    log.info("═══════════════════════════════════════════════════════");
                } catch (Exception e) {
                    log.error(" Failed to create default admin user: {}", e.getMessage());
                }
            }
        );
    }

    private void createTestUsers() {
        // Test landlord
        userRepository.findByEmail("landlord@test.com").ifPresentOrElse(
            existing -> ensureRbacRole(existing, "ROLE_LANDLORD"),
            () -> {
                try {
                    User landlord = new User();
                    landlord.setEmail("landlord@test.com");
                    landlord.setPasswordHash(passwordEncoder.encode("Landlord@123"));
                    landlord.setFirstName("John");
                    landlord.setLastName("Landlord");
                    landlord.setPhoneNumber("+237123456789");
                    landlord.setRole(UserRole.LANDLORD);
                    assignRbacRole(landlord, "ROLE_LANDLORD");
                    landlord.setIsActive(true);
                    landlord.setIsVerified(true);
                    landlord.setLanguagePreference("en");
                    userRepository.save(landlord);
                    log.info(" Test Landlord: landlord@test.com / Landlord@123");
                } catch (Exception e) {
                    log.warn(" Could not create test landlord: {}", e.getMessage());
                }
            }
        );

        // Test tenant
        userRepository.findByEmail("tenant@test.com").ifPresentOrElse(
            existing -> ensureRbacRole(existing, "ROLE_TENANT"),
            () -> {
                try {
                    User tenant = new User();
                    tenant.setEmail("tenant@test.com");
                    tenant.setPasswordHash(passwordEncoder.encode("Tenant@123"));
                    tenant.setFirstName("Jane");
                    tenant.setLastName("Tenant");
                    tenant.setPhoneNumber("+237987654321");
                    tenant.setRole(UserRole.TENANT);
                    assignRbacRole(tenant, "ROLE_TENANT");
                    tenant.setIsActive(true);
                    tenant.setIsVerified(true);
                    tenant.setLanguagePreference("en");
                    userRepository.save(tenant);
                    log.info(" Test Tenant: tenant@test.com / Tenant@123");
                } catch (Exception e) {
                    log.warn(" Could not create test tenant: {}", e.getMessage());
                }
            }
        );
    }

    /** Assigns the RBAC role to the user if they don't already have it. */
    private void assignRbacRole(User user, String roleName) {
        roleRepository.findByName(roleName).ifPresentOrElse(
            r -> user.getRoles().add(r),
            () -> log.error(" RBAC role '{}' not found in DB — V28 migration may not have run. " +
                "User {} will lack permissions.", roleName, user.getEmail())
        );
    }

    /** Idempotent: adds the RBAC role to an existing user if they're missing it (broken-state recovery). */
    private void ensureRbacRole(User user, String roleName) {
        boolean hasRole = user.getRoles().stream().anyMatch(r -> roleName.equals(r.getName()));
        if (!hasRole) {
            log.warn(" Existing user {} is missing RBAC role '{}' — backfilling now.", user.getEmail(), roleName);
            assignRbacRole(user, roleName);
            userRepository.save(user);
        }
    }
}