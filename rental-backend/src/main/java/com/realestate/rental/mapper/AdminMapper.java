package com.realestate.rental.mapper;

import com.realestate.rental.dto.response.TopPropertyDto;
import com.realestate.rental.domain.entity.Property;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    TopPropertyDto toTopPropertyDto(Property property);

    List<TopPropertyDto> toTopPropertyDtoList(List<Property> properties);
}
