package com.homeflex.core.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import com.homeflex.core.domain.enums.UserRole;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    @Column(name = "entity_version")
    private Long version;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "phone_number", length = 200) // Increased length for Base64 encrypted string
    @Convert(converter = com.homeflex.core.security.PiiEncryptionConverter.class)
    private String phoneNumber;

    @Column(name = "first_name", nullable = false, length = 200)
    @Convert(converter = com.homeflex.core.security.PiiEncryptionConverter.class)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 200)
    @Convert(converter = com.homeflex.core.security.PiiEncryptionConverter.class)
    private String lastName;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Deprecated(since = "v2.0", forRemoval = true)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "stripe_account_id")
    private String stripeAccountId;

    @Column(name = "language_preference", length = 5)
    private String languagePreference = "en";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Agency agency;

    @Column(name = "agency_role", length = 20)
    private String agencyRole;

    @Column(name = "trust_score")
    private Double trustScore = 5.0;

    @Column(name = "email_notifications_enabled")
    private Boolean emailNotificationsEnabled = true;

    @Column(name = "push_notifications_enabled")
    private Boolean pushNotificationsEnabled = true;

    @Column(name = "sms_notifications_enabled")
    private Boolean smsNotificationsEnabled = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
