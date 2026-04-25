package com.homeflex.core.dto.request;

import com.homeflex.core.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phoneNumber,
        @NotNull UserRole role,
        Boolean dualRole  // if true, user also gets the complementary role (landlord↔tenant)
) {}
