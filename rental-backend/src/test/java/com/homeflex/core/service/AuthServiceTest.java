package com.homeflex.core.service;

import com.homeflex.core.domain.entity.EmailVerificationToken;
import com.homeflex.core.domain.entity.PasswordResetToken;
import com.homeflex.core.domain.entity.RefreshToken;
import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.UserRole;
import com.homeflex.core.domain.repository.EmailVerificationTokenRepository;
import com.homeflex.core.domain.repository.OAuthProviderRepository;
import com.homeflex.core.domain.repository.PasswordResetTokenRepository;
import com.homeflex.core.domain.repository.RefreshTokenRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.dto.request.LoginRequest;
import com.homeflex.core.dto.request.RegisterRequest;
import com.homeflex.core.dto.response.AuthTokens;
import com.homeflex.core.dto.response.UserDto;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.mapper.UserMapper;
import com.homeflex.core.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Mock private OAuthProviderRepository oAuthProviderRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private EmailService emailService;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshTokenExpiration", 604800000L);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(UserRole.TENANT);
        testUser.setIsActive(true);
        testUser.setIsVerified(false);

        testUserDto = new UserDto(
                testUser.getId(), testUser.getEmail(), testUser.getFirstName(),
                testUser.getLastName(), null, null, "TENANT",
                true, false, "en", null
        );
    }

    // ── Register ───────────────────────────────────────────────────────

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest(
                "new@example.com", "password123", "Jane", "Doe", null, UserRole.TENANT
        );

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toDto(any(User.class))).thenReturn(testUserDto);

        AuthTokens result = authService.register(request);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.user()).isEqualTo(testUserDto);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendVerificationEmail(any(User.class));
    }

    @Test
    void register_duplicateEmail_throwsConflict() {
        RegisterRequest request = new RegisterRequest(
                "exists@example.com", "password123", "Jane", "Doe", null, UserRole.TENANT
        );
        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already registered");
    }

    // ── Login ──────────────────────────────────────────────────────────

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest("test@example.com", "password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.generateToken(testUser)).thenReturn("access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        AuthTokens result = authService.login(request);

        assertThat(result.accessToken()).isEqualTo("access-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_badCredentials_throws() {
        LoginRequest request = new LoginRequest("test@example.com", "wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_suspendedUser_throwsDomain() {
        testUser.setIsActive(false);
        LoginRequest request = new LoginRequest("test@example.com", "password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("suspended");
    }

    // ── Verify Email ───────────────────────────────────────────────────

    @Test
    void verifyEmail_success() {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(testUser);
        token.setToken("verify-token");
        token.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(emailVerificationTokenRepository.findByToken("verify-token"))
                .thenReturn(Optional.of(token));

        authService.verifyEmail("verify-token");

        assertThat(testUser.getIsVerified()).isTrue();
        verify(userRepository).save(testUser);
        verify(emailVerificationTokenRepository).save(token);
        assertThat(token.getVerifiedAt()).isNotNull();
    }

    @Test
    void verifyEmail_invalidToken_throws() {
        when(emailVerificationTokenRepository.findByToken("bad"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.verifyEmail("bad"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Invalid");
    }

    @Test
    void verifyEmail_expiredToken_throws() {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(testUser);
        token.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(emailVerificationTokenRepository.findByToken("expired"))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.verifyEmail("expired"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void verifyEmail_alreadyVerified_throws() {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(testUser);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setVerifiedAt(LocalDateTime.now().minusMinutes(5));

        when(emailVerificationTokenRepository.findByToken("used"))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.verifyEmail("used"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("already been verified");
    }

    // ── Password Reset ─────────────────────────────────────────────────

    @Test
    void sendPasswordResetEmail_success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
                .thenAnswer(i -> i.getArgument(0));

        authService.sendPasswordResetEmail("test@example.com");

        verify(passwordResetTokenRepository).deleteByUserId(testUser.getId());
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq(testUser), anyString());
    }

    @Test
    void sendPasswordResetEmail_unknownUser_throws() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.sendPasswordResetEmail("unknown@example.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void resetPassword_success() {
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(testUser);
        token.setToken("reset-token");
        token.setExpiresAt(LocalDateTime.now().plusMinutes(30));

        when(passwordResetTokenRepository.findByToken("reset-token"))
                .thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newPassword")).thenReturn("newHash");

        authService.resetPassword("reset-token", "newPassword");

        assertThat(testUser.getPasswordHash()).isEqualTo("newHash");
        verify(userRepository).save(testUser);
        assertThat(token.getUsedAt()).isNotNull();
        verify(refreshTokenRepository).deleteByUserId(testUser.getId());
    }

    @Test
    void resetPassword_expiredToken_throws() {
        PasswordResetToken token = new PasswordResetToken();
        token.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(passwordResetTokenRepository.findByToken("expired"))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.resetPassword("expired", "newPassword"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("expired");
    }

    @Test
    void resetPassword_usedToken_throws() {
        PasswordResetToken token = new PasswordResetToken();
        token.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        token.setUsedAt(LocalDateTime.now().minusMinutes(5));

        when(passwordResetTokenRepository.findByToken("used"))
                .thenReturn(Optional.of(token));

        assertThatThrownBy(() -> authService.resetPassword("used", "newPassword"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("already been used");
    }

    // ── Refresh Token ──────────────────────────────────────────────────

    @Test
    void refreshToken_success() {
        RefreshToken rt = new RefreshToken();
        rt.setUser(testUser);
        rt.setToken("refresh-token");
        rt.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(refreshTokenRepository.findByToken("refresh-token")).thenReturn(Optional.of(rt));
        when(jwtTokenProvider.generateToken(testUser)).thenReturn("new-access-token");
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        AuthTokens result = authService.refreshToken("refresh-token");

        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void refreshToken_expired_throws() {
        RefreshToken rt = new RefreshToken();
        rt.setUser(testUser);
        rt.setToken("expired-refresh");
        rt.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(refreshTokenRepository.findByToken("expired-refresh")).thenReturn(Optional.of(rt));

        assertThatThrownBy(() -> authService.refreshToken("expired-refresh"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("expired");
    }

    // ── Logout ─────────────────────────────────────────────────────────

    @Test
    void logout_deletesRefreshTokens() {
        UUID userId = UUID.randomUUID();
        authService.logout(userId);
        verify(refreshTokenRepository).deleteByUserId(userId);
    }
}
