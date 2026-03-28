package com.realestate.rental.api.v1;

import com.realestate.rental.dto.response.*;
import com.realestate.rental.dto.request.ChangePasswordRequest;
import com.realestate.rental.dto.common.ApiValueResponse;
import com.realestate.rental.dto.request.LanguageUpdateRequest;
import com.realestate.rental.dto.request.UserUpdateRequest;
import com.realestate.rental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(
            @Valid @RequestBody UserUpdateRequest request,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<UserDto> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(userService.updateAvatar(userId, file));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiValueResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        userService.changePassword(userId, request.currentPassword(), request.newPassword());
        return ResponseEntity.ok(new ApiValueResponse<>("Password changed successfully"));
    }

    @PutMapping("/me/language")
    public ResponseEntity<UserDto> updateLanguage(
            @RequestBody LanguageUpdateRequest request,
            Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());
        return ResponseEntity.ok(userService.updateLanguage(userId, request.language()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}