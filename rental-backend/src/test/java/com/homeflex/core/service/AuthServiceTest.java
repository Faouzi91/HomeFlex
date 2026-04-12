package com.homeflex.core.service;

import com.homeflex.core.domain.entity.RefreshToken;
import com.homeflex.core.domain.entity.User;
import com.homeflex.core.domain.enums.UserRole;
import com.homeflex.core.domain.repository.EmailVerificationTokenRepository;
import com.homeflex.core.domain.repository.PasswordResetTokenRepository;
import com.homeflex.core.domain.repository.RefreshTokenRepository;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.dto.request.LoginRequest;
import com.homeflex.core.dto.request.RegisterRequest;
import com.homeflex.core.dto.response.AuthTokens;
import com.homeflex.core.dto.response.UserDto;
import com.homeflex.core.exception.ConflictException;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.mapper.UserMapper;
import com.homeflex.core.security.JwtTokenProvider;
import com.homeflex.core.security.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider tokenProvider;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private EmailService emailService;
    @Mock private UserMapper userMapper;
    @Mock private LoginAttemptService loginAttemptService;

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
                true, false, "en", null, null, 5.0, 100, LocalDateTime.now()
        );
    }

    @Test
    void login_success_returnsAuthTokens() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        Authentication auth = mock(Authentication.class);

        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(testUser));
        when(tokenProvider.generateToken(any(User.class))).thenReturn("token");
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        AuthTokens response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("token");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(loginAttemptService).loginSucceeded(request.email());
    }

    @Test
    void login_blockedUser_throwsDomainException() {
        LoginRequest request = new LoginRequest("blocked@example.com", "password");
        when(loginAttemptService.isBlocked(request.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("locked");
    }

    @Test
    void register_success_returnsAuthTokens() {
        RegisterRequest request = new RegisterRequest(
                "new@example.com", "password123", "Jane", "Doe", "123456789", UserRole.TENANT
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(tokenProvider.generateToken(any())).thenReturn("token");
        when(userMapper.toDto(any())).thenReturn(testUserDto);

        AuthTokens response = authService.register(request);

        assertThat(response).isNotNull();
        verify(emailService).sendVerificationEmail(eq(testUser));
    }

    @Test
    void register_duplicateEmail_throwsConflict() {
        RegisterRequest request = new RegisterRequest("test@example.com", "pass", "A", "B", null, UserRole.TENANT);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void logout_deletesRefreshTokens() {
        UUID userId = UUID.randomUUID();
        authService.logout(userId);
        verify(refreshTokenRepository).deleteByUserId(userId);
    }
}
