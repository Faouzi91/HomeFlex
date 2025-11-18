// ====================================
// SOLUTION 1: Update AuthService.java
// Make Google OAuth optional with default values
// ====================================
package com.realestate.rental.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.realestate.rental.dto.*;
import com.realestate.rental.dto.request.LoginRequest;
import com.realestate.rental.dto.request.RegisterRequest;
import com.realestate.rental.repository.*;
import com.realestate.rental.security.JwtTokenProvider;
import com.realestate.rental.utils.entity.OAuthProvider;
import com.realestate.rental.utils.entity.RefreshToken;
import com.realestate.rental.utils.entity.User;
import com.realestate.rental.utils.enumeration.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

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

    // FIXED: Add default value to make it optional
    @Value("${app.google.client-id:dummy-client-id-for-development}")
    private String googleClientId;

    @Value("${app.jwt.refresh-expiration}")
    private Long refreshTokenExpiration;

    public AuthResponse register(RegisterRequest request) {
        // Check if user exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        user.setIsActive(true);
        user.setIsVerified(false);
        user.setLanguagePreference("en");

        user = userRepository.save(user);

        // Send verification email (with try-catch to not block registration)
        try {
            emailService.sendVerificationEmail(user);
        } catch (Exception e) {
            System.err.println("Failed to send verification email: " + e.getMessage());
        }

        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserDto(user))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getIsActive()) {
            throw new RuntimeException("Account is suspended");
        }

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Generate tokens
        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserDto(user))
                .build();
    }

    public AuthResponse googleLogin(String idTokenString) {
        // FIXED: Check if Google OAuth is properly configured
        if ("dummy-client-id-for-development".equals(googleClientId)) {
            throw new RuntimeException("Google OAuth is not configured. Please set GOOGLE_CLIENT_ID environment variable.");
        }

        try {
            // Verify Google token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new RuntimeException("Invalid Google token");
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

            return AuthResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .user(mapToUserDto(user))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }

    public AuthResponse refreshToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.generateToken(user);

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshTokenString)
                .user(mapToUserDto(user))
                .build();
    }

    public void logout(String token) {
        // Extract user from token
        String userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));

        // Delete all refresh tokens for user
        refreshTokenRepository.deleteByUserId(UUID.fromString(userId));
    }

    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String resetToken = UUID.randomUUID().toString();

        // Save reset token (you might want a separate entity for this)
        try {
            emailService.sendPasswordResetEmail(user, resetToken);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
            throw new RuntimeException("Failed to send password reset email");
        }
    }

    public void resetPassword(String token, String newPassword) {
        // Verify token and update password
        // Implementation depends on how you store reset tokens
        throw new RuntimeException("Password reset not yet implemented");
    }

    private String createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000));

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    private UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .languagePreference(user.getLanguagePreference())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

