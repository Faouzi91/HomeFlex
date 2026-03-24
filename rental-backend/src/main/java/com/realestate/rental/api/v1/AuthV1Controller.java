package com.realestate.rental.api.v1;

import com.realestate.rental.application.user.AuthApplicationService;
import com.realestate.rental.dto.AuthResponse;
import com.realestate.rental.dto.request.ForgotPasswordRequest;
import com.realestate.rental.dto.request.GoogleLoginRequest;
import com.realestate.rental.dto.request.LoginRequest;
import com.realestate.rental.dto.request.MessageResponse;
import com.realestate.rental.dto.request.RefreshTokenRequest;
import com.realestate.rental.dto.request.RegisterRequest;
import com.realestate.rental.dto.request.ResetPasswordRequest;
import com.realestate.rental.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthV1Controller {

    private final AuthApplicationService authApplicationService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authApplicationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authApplicationService.login(request));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleLoginRequest request) {
        return ResponseEntity.ok(authApplicationService.googleLogin(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authApplicationService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authApplicationService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.sendPasswordResetEmail(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("Password reset email sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password reset successful"));
    }
}
