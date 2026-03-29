package com.homeflex.core.api.v1;

import com.homeflex.core.config.AppProperties;
import com.homeflex.core.dto.response.AuthResponse;
import com.homeflex.core.dto.response.AuthTokens;
import com.homeflex.core.dto.common.ApiValueResponse;
import com.homeflex.core.dto.request.ForgotPasswordRequest;
import com.homeflex.core.dto.request.GoogleLoginRequest;
import com.homeflex.core.dto.request.LoginRequest;
import com.homeflex.core.dto.request.RegisterRequest;
import com.homeflex.core.dto.request.ResetPasswordRequest;
import com.homeflex.core.security.JwtTokenProvider;
import com.homeflex.core.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthV1Controller {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppProperties appProperties;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                  HttpServletResponse response) {
        AuthTokens tokens = authService.register(request);
        addAuthCookies(response, tokens);
        return ResponseEntity.ok(new AuthResponse(tokens.user()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        AuthTokens tokens = authService.login(request);
        addAuthCookies(response, tokens);
        return ResponseEntity.ok(new AuthResponse(tokens.user()));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request,
                                                     HttpServletResponse response) {
        AuthTokens tokens = authService.googleLogin(request.idToken());
        addAuthCookies(response, tokens);
        return ResponseEntity.ok(new AuthResponse(tokens.user()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "REFRESH_TOKEN", required = false) String cookieToken,
            HttpServletResponse response) {
        if (cookieToken == null) {
            return ResponseEntity.badRequest().build();
        }
        AuthTokens tokens = authService.refreshToken(cookieToken);
        addAuthCookies(response, tokens);
        return ResponseEntity.ok(new AuthResponse(tokens.user()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            authService.logout(UUID.fromString(auth.getName()));
        }
        clearAuthCookies(response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiValueResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.sendPasswordResetEmail(request.email());
        return ResponseEntity.ok(new ApiValueResponse<>("Password reset email sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiValueResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(new ApiValueResponse<>("Password reset successful"));
    }

    // ── Cookie helpers ──────────────────────────────────────────────────

    private void addAuthCookies(HttpServletResponse response, AuthTokens tokens) {
        var cookieCfg = appProperties.getJwt().getCookie();

        Cookie access = new Cookie(cookieCfg.getAccessTokenName(), tokens.accessToken());
        access.setHttpOnly(true);
        access.setSecure(cookieCfg.isSecure());
        access.setPath("/");
        access.setMaxAge(jwtTokenProvider.getAccessTokenMaxAgeSeconds());
        access.setAttribute("SameSite", cookieCfg.getSameSite());
        response.addCookie(access);

        Cookie refresh = new Cookie(cookieCfg.getRefreshTokenName(), tokens.refreshToken());
        refresh.setHttpOnly(true);
        refresh.setSecure(cookieCfg.isSecure());
        refresh.setPath("/api/v1/auth");
        refresh.setMaxAge(cookieCfg.getMaxAgeSeconds());
        refresh.setAttribute("SameSite", cookieCfg.getSameSite());
        response.addCookie(refresh);
    }

    private void clearAuthCookies(HttpServletResponse response) {
        var cookieCfg = appProperties.getJwt().getCookie();

        Cookie access = new Cookie(cookieCfg.getAccessTokenName(), "");
        access.setHttpOnly(true);
        access.setSecure(cookieCfg.isSecure());
        access.setPath("/");
        access.setMaxAge(0);
        access.setAttribute("SameSite", cookieCfg.getSameSite());
        response.addCookie(access);

        Cookie refresh = new Cookie(cookieCfg.getRefreshTokenName(), "");
        refresh.setHttpOnly(true);
        refresh.setSecure(cookieCfg.isSecure());
        refresh.setPath("/api/v1/auth");
        refresh.setMaxAge(0);
        refresh.setAttribute("SameSite", cookieCfg.getSameSite());
        response.addCookie(refresh);
    }
}
