package com.realestate.rental.mapper;

import com.realestate.rental.dto.response.UserDto;
import com.realestate.rental.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    UserDto toDto(User user);
}
