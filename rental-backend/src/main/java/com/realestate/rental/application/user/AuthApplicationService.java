package com.realestate.rental.application.user;

import com.realestate.rental.dto.AuthResponse;
import com.realestate.rental.dto.request.GoogleLoginRequest;
import com.realestate.rental.dto.request.LoginRequest;
import com.realestate.rental.dto.request.RefreshTokenRequest;
import com.realestate.rental.dto.request.RegisterRequest;
import com.realestate.rental.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final AuthService authService;

    public AuthResponse register(RegisterRequest request) {
        return authService.register(request);
    }

    public AuthResponse login(LoginRequest request) {
        return authService.login(request);
    }

    public AuthResponse googleLogin(GoogleLoginRequest request) {
        return authService.googleLogin(request.idToken());
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        return authService.refreshToken(request.refreshToken());
    }

    public void logout(String authHeader) {
        authService.logout(authHeader);
    }
}
