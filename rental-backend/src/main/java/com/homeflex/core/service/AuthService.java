package com.homeflex.core.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.homeflex.core.mapper.UserMapper;
import com.homeflex.core.dto.response.AuthTokens;
import com.homeflex.core.dto.request.LoginRequest;
import com.homeflex.core.dto.request.RegisterRequest;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.domain.repository.RoleRepository;
import com.homeflex.core.domain.repository.EmailVerificationTokenRepository;
import com.homeflex.core.domain.repository.PasswordResetTokenRepository;
import com.homeflex.core.domain.repository.RefreshTokenRepository;
import com.homeflex.core.domain.repository.OAuthProviderRepository;
import com.homeflex.core.security.JwtTokenProvider;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.domain.entity.EmailVerificationToken;
import com.homeflex.core.domain.entity.OAuthProvider;
import com.homeflex.core.domain.entity.PasswordResetToken;
import com.homeflex.core.domain.entity.RefreshToken;
import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.UserRole;
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
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final OAuthProviderRepository oAuthProviderRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final com.homeflex.core.security.LoginAttemptService loginAttemptService;

    @Value("${app.google.client-id:dummy-client-id-for-development}")
    private String googleClientId;

    @Value("${app.jwt.refresh-expiration}")
    private Long refreshTokenExpiration;

    public AuthTokens register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setRole(request.role());
        assignRbacRole(user, "ROLE_" + request.role().name());
        user.setIsActive(true);
        user.setIsVerified(false);
        user.setLanguagePreference("en");

        user = userRepository.save(user);

        try {
            emailService.sendVerificationEmail(user);
        } catch (Exception e) {
            log.warn("Failed to send verification email: {}", e.getMessage());
        }

        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return new AuthTokens(accessToken, refreshToken, userMapper.toDto(user));
    }

    public AuthTokens login(LoginRequest request) {
        if (loginAttemptService.isBlocked(request.email())) {
            throw new DomainException("Your account is temporarily locked due to too many failed login attempts. Please try again later.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            loginAttemptService.loginFailed(request.email());
            throw e;
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getIsActive()) {
            throw new DomainException("Account is suspended");
        }

        loginAttemptService.loginSucceeded(request.email());
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = createRefreshToken(user);

        return new AuthTokens(accessToken, refreshToken, userMapper.toDto(user));
    }

    public AuthTokens googleLogin(String idTokenString) {
        if ("dummy-client-id-for-development".equals(googleClientId)) {
            throw new DomainException("Google OAuth is not configured. Please set GOOGLE_CLIENT_ID environment variable.");
        }

        try {
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

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setFirstName(firstName);
                        newUser.setLastName(lastName);
                        newUser.setProfilePictureUrl(pictureUrl);
                        newUser.setRole(UserRole.TENANT);
                        assignRbacRole(newUser, "ROLE_TENANT");
                        newUser.setIsActive(true);
                        newUser.setIsVerified(true);
                        return userRepository.save(newUser);
                    });

            oAuthProviderRepository.findByProviderAndProviderUserId("GOOGLE", googleUserId)
                    .orElseGet(() -> {
                        OAuthProvider provider = new OAuthProvider();
                        provider.setUser(user);
                        provider.setProvider("GOOGLE");
                        provider.setProviderUserId(googleUserId);
                        return oAuthProviderRepository.save(provider);
                    });

            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            String accessToken = jwtTokenProvider.generateToken(user);
            String refreshToken = createRefreshToken(user);

            return new AuthTokens(accessToken, refreshToken, userMapper.toDto(user));

        } catch (Exception e) {
            throw new DomainException("Google authentication failed: " + e.getMessage());
        }
    }

    public AuthTokens appleLogin(String idToken) {
        throw new DomainException("Apple OAuth is not configured. Please set APPLE_CLIENT_ID environment variable.");
    }

    public AuthTokens facebookLogin(String accessToken) {
        throw new DomainException("Facebook OAuth is not configured. Please set FACEBOOK_APP_ID environment variable.");
    }

    public AuthTokens refreshToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
                .orElseThrow(() -> new DomainException("Invalid refresh token"));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new DomainException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.generateToken(user);

        return new AuthTokens(newAccessToken, refreshTokenString, userMapper.toDto(user));
    }

    public void logout(UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new DomainException("Invalid verification token"));

        if (verificationToken.isExpired()) {
            throw new DomainException("Verification token has expired");
        }
        if (verificationToken.isVerified()) {
            throw new DomainException("Email has already been verified");
        }

        User user = verificationToken.getUser();
        user.setIsVerified(true);
        userRepository.save(user);

        verificationToken.setVerifiedAt(LocalDateTime.now());
        emailVerificationTokenRepository.save(verificationToken);

        log.info("Email verified for user {}", user.getId());
    }

    public void sendPasswordResetEmail(String email) {
        // Silently ignore non-existent emails to prevent user enumeration
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return;
        }

        // Invalidate any existing reset tokens for this user
        passwordResetTokenRepository.deleteByUserId(user.getId());

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        passwordResetTokenRepository.save(resetToken);

        try {
            emailService.sendPasswordResetEmail(user, resetToken.getToken());
        } catch (Exception e) {
            log.error("Failed to send password reset email: {}", e.getMessage());
            throw new DomainException("Failed to send password reset email");
        }
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new DomainException("Invalid password reset token"));

        if (resetToken.isExpired()) {
            throw new DomainException("Password reset token has expired");
        }
        if (resetToken.isUsed()) {
            throw new DomainException("Password reset token has already been used");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);

        // Invalidate all refresh tokens so existing sessions are logged out
        refreshTokenRepository.deleteByUserId(user.getId());

        log.info("Password reset completed for user {}", user.getId());
    }

    private void assignRbacRole(User user, String roleName) {
        roleRepository.findByName(roleName).ifPresent(r -> user.getRoles().add(r));
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
