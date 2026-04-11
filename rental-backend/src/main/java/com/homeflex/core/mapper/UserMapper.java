package com.homeflex.core.mapper;

import com.homeflex.core.dto.response.UserDto;
import com.homeflex.core.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    @Mapping(target = "agencyId", source = "agency.id")
    UserDto toDto(User user);
}
