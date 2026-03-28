// ====================================
// SOLUTION 1: Update AuthService.java
// Make Google OAuth optional with default values
// ====================================
package com.realestate.rental.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.realestate.rental.mapper.UserMapper;
import com.realestate.rental.dto.response.*;
import com.realestate.rental.dto.request.LoginRequest;
import com.realestate.rental.dto.request.RegisterRequest;
import com.realestate.rental.domain.repository.*;
import com.realestate.rental.security.JwtTokenProvider;
import com.realestate.rental.exception.ConflictException;
import com.realestate.rental.exception.DomainException;
import com.realestate.rental.exception.ResourceNotFoundException;
import com.realestate.rental.domain.entity.OAuthProvider;
import com.realestate.rental.domain.entity.RefreshToken;
import com.realestate.rental.domain.entity.User;
import com.realestate.rental.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuthProviderRepository oAuthProviderRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserMapper userMapper;

    // FIXED: Add default value to make it optional
    @Value("${app.google.client-id:dummy-client-id-for-development}")
    private String googleClientId;

    @Value("${app.jwt.refresh-expiration}")
    private Long refreshTokenExpiration;

    public AuthResponse register(RegisterRequest request) {
        // Check if user exists
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already registered");
        }

        // Create user
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setRole(request.role());
        user.setIsActive(true);
        user.setIsVerified(false);
        user.setLanguagePreference("en");

        user = userRepository.save(user);

        // Send verification email (with try-catch to not block registration)
        try {
            emailService.sendVerificationEmail(user);
        } catch (Exception e) {
            log.warn("Failed to send verification email: {}", e.getMessage());
        }

        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, userMapper.toDto(user));
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // Get user
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getIsActive()) {
            throw new DomainException("Account is suspended");
        }

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, userMapper.toDto(user));
    }

    public AuthResponse googleLogin(String idTokenString) {
        // FIXED: Check if Google OAuth is properly configured
        if ("dummy-client-id-for-development".equals(googleClientId)) {
            throw new DomainException("Google OAuth is not configured. Please set GOOGLE_CLIENT_ID environment variable.");
        }

        try {
            // Verify Google token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new DomainException("Invalid Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String googleUserId = payload.getSubject();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String pictureUrl = (String) payload.get("picture");

            // Find or create user
            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setFirstName(firstName);
                        newUser.setLastName(lastName);
                        newUser.setProfilePictureUrl(pictureUrl);
                        newUser.setRole(UserRole.TENANT);
                        newUser.setIsActive(true);
                        newUser.setIsVerified(true);
                        return userRepository.save(newUser);
                    });

            // Save OAuth provider info
            oAuthProviderRepository.findByProviderAndProviderUserId("GOOGLE", googleUserId)
                    .orElseGet(() -> {
                        OAuthProvider provider = new OAuthProvider();
                        provider.setUser(user);
                        provider.setProvider("GOOGLE");
                        provider.setProviderUserId(googleUserId);
                        return oAuthProviderRepository.save(provider);
                    });

            // Update last login
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // Generate tokens
            String accessToken = jwtTokenProvider.generateToken(user);
            String refreshToken = createRefreshToken(user);

            return new AuthResponse(accessToken, refreshToken, userMapper.toDto(user));

        } catch (Exception e) {
            throw new DomainException("Google authentication failed: " + e.getMessage());
        }
    }

    public AuthResponse refreshToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new DomainException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new DomainException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.generateToken(user);

        return new AuthResponse(newAccessToken, refreshTokenString, userMapper.toDto(user));
    }

    public void logout(String token) {
        // Extract user from token
        String userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));

        // Delete all refresh tokens for user
        refreshTokenRepository.deleteByUserId(UUID.fromString(userId));
    }

    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String resetToken = UUID.randomUUID().toString();

        // Save reset token (you might want a separate entity for this)
        try {
            emailService.sendPasswordResetEmail(user, resetToken);
        } catch (Exception e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
            throw new DomainException("Failed to send password reset email");
        }
    }

    public void resetPassword(String token, String newPassword) {
        // Verify token and update password
        // Implementation depends on how you store reset tokens
        throw new DomainException("Password reset not yet implemented");
    }

    private String createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000));

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

}

