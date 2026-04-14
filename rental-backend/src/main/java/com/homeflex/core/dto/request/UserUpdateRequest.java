package com.homeflex.core.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters") String firstName,
        @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters") String lastName,
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format") String phoneNumber,
        String languagePreference,
        Boolean emailNotificationsEnabled,
        Boolean pushNotificationsEnabled,
        Boolean smsNotificationsEnabled
) {}
