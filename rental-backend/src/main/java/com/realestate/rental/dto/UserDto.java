package com.realestate.rental.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

// UserDto.java
@Data
@Builder
public class UserDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePictureUrl;
    private String role;
    private Boolean isActive;
    private Boolean isVerified;
    private String languagePreference;
    private LocalDateTime createdAt;
}
