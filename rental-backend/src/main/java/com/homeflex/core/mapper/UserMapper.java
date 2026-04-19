package com.homeflex.core.mapper;

import com.homeflex.core.dto.response.UserDto;
import com.homeflex.core.domain.entity.Role;
import com.homeflex.core.domain.entity.Permission;
import com.homeflex.core.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(com.homeflex.core.domain.entity.Role::getName).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "permissions", expression = "java(user.getRoles().stream().flatMap(r -> r.getPermissions().stream()).map(com.homeflex.core.domain.entity.Permission::getName).distinct().collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "agencyId", source = "agency.id")
    @Mapping(target = "profileCompleteness", ignore = true)
    UserDto toDto(User user);
}
