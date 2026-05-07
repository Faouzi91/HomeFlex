package com.homeflex.core.mapper;

import com.homeflex.core.dto.response.UserDto;
import com.homeflex.core.domain.entity.Role;
import com.homeflex.core.domain.entity.Permission;
import com.homeflex.core.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(com.homeflex.core.domain.entity.Role::getName).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "permissions", expression = "java(user.getRoles().stream().flatMap(r -> r.getPermissions().stream()).map(com.homeflex.core.domain.entity.Permission::getName).distinct().collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "agencyId", source = "agency.id")
    @Mapping(target = "profileCompleteness", ignore = true)
    @Mapping(target = "stripeConnected", expression = "java(user.getStripeAccountId() != null)")
    UserDto toDto(User user);

    /**
     * Embedded variant for public-facing payloads (PropertyDto.landlord, ReviewDto.reviewer,
     * BookingDto.tenant when seen by counterparties, etc.). Strips PII (email, phoneNumber,
     * notification prefs, Stripe account id, agency role) so it never leaks via embedded views.
     */
    @Named("public")
    default UserDto toPublicDto(User user) {
        if (user == null) return null;
        UserDto full = toDto(user);
        return new UserDto(
                full.id(),
                null,                  // email
                full.firstName(),
                full.lastName(),
                null,                  // phoneNumber
                full.profilePictureUrl(),
                full.role(),
                full.roles(),
                java.util.List.of(),   // permissions
                full.isActive(),
                full.isVerified(),
                null,                  // languagePreference
                full.agencyId(),
                null,                  // agencyRole
                full.trustScore(),
                null,                  // emailNotificationsEnabled
                null,                  // pushNotificationsEnabled
                null,                  // smsNotificationsEnabled
                null,                  // profileCompleteness
                full.createdAt(),
                full.stripeConnected(),
                null                   // stripeAccountId
        );
    }
}
