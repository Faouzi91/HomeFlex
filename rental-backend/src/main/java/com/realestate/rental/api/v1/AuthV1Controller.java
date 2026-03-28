package com.realestate.rental.api.v1;

import com.realestate.rental.dto.response.AuthResponse;
import com.realestate.rental.dto.common.ApiValueResponse;
import com.realestate.rental.dto.request.ForgotPasswordRequest;
import com.realestate.rental.dto.request.GoogleLoginRequest;
import com.realestate.rental.dto.request.LoginRequest;
import com.realestate.rental.dto.request.RefreshTokenRequest;
import com.realestate.rental.dto.request.RegisterRequest;
import com.realestate.rental.dto.request.ResetPasswordRequest;
import com.realestate.rental.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Auth endpoints — refresh token stored in httpOnly cookie per SRS NFR-SEC1.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthV1Controller {

    private final AuthService authService;

    @Value("${app.jwt.cookie.refresh-token-name:refreshToken}")
    private String refreshTokenCookieName;

    @Value("${app.jwt.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${app.jwt.cookie.max-age-seconds:604800}")
    private int cookieMaxAge;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                  HttpServletResponse response) {
        AuthResponse auth = authService.register(request);
        addRefreshTokenCookie(response, auth.refreshToken());
        return ResponseEntity.ok(withoutRefreshToken(auth));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        AuthResponse auth = authService.login(request);
        addRefreshTokenCookie(response, auth.refreshToken());
        return ResponseEntity.ok(withoutRefreshToken(auth));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@Valid @RequestBody GoogleLoginRequest request,
                                                     HttpServletResponse response) {
        AuthResponse auth = authService.googleLogin(request.idToken());
        addRefreshTokenCookie(response, auth.refreshToken());
        return ResponseEntity.ok(withoutRefreshToken(auth));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String cookieToken,
            @RequestBody(required = false) RefreshTokenRequest bodyRequest,
            HttpServletResponse response) {
        String token = cookieToken != null ? cookieToken :
                (bodyRequest != null ? bodyRequest.refreshToken() : null);
        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        AuthResponse auth = authService.refreshToken(token);
        addRefreshTokenCookie(response, auth.refreshToken());
        return ResponseEntity.ok(withoutRefreshToken(auth));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token,
                                        HttpServletResponse response) {
        authService.logout(token);
        clearRefreshTokenCookie(response);
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

    private void addRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(refreshTokenCookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(cookieMaxAge);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(refreshTokenCookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/v1/auth");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private AuthResponse withoutRefreshToken(AuthResponse auth) {
        return new AuthResponse(auth.token(), null, auth.user());
    }
}
