package com.homeflex.features.property.dto.request;

import com.homeflex.core.domain.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record RoleChangeRequest(@NotNull UserRole role) {}
