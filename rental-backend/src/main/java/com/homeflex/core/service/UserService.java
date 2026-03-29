package com.homeflex.core.service;

import com.homeflex.core.mapper.UserMapper;
import com.homeflex.core.dto.response.UserDto;
import com.homeflex.core.dto.request.UserUpdateRequest;
import com.homeflex.core.domain.repository.UserRepository;
import com.homeflex.core.exception.DomainException;
import com.homeflex.core.exception.ResourceNotFoundException;
import com.homeflex.core.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;
    private final UserMapper userMapper;

    public UserDto getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userMapper.toDto(user);
    }

    public UserDto updateUser(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }

        if (request.languagePreference() != null) {
            user.setLanguagePreference(request.languagePreference());
        }

        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto updateAvatar(UUID userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate file
        if (!file.getContentType().startsWith("image/")) {
            throw new DomainException("File must be an image");
        }

        // Upload to storage
        String avatarUrl = storageService.uploadFile(file, "avatars");

        // Update user
        user.setProfilePictureUrl(avatarUrl);
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify current password
        if (user.getPasswordHash() == null ||
                !passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new DomainException("Current password is incorrect");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public UserDto updateLanguage(UUID userId, String language) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setLanguagePreference(language);
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

}
