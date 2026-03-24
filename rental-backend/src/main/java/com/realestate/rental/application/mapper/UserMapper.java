package com.realestate.rental.application.mapper;

import com.realestate.rental.dto.UserDto;
import com.realestate.rental.utils.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    UserDto toDto(User user);
}
