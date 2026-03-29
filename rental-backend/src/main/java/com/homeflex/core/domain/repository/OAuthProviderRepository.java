package com.homeflex.core.domain.repository;

import com.homeflex.core.domain.entity.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

// OAuthProviderRepository.java
@Repository
public interface OAuthProviderRepository extends JpaRepository<OAuthProvider, UUID> {
    Optional<OAuthProvider> findByProviderAndProviderUserId(String provider, String providerUserId);
}
