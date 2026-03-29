package com.homeflex.features.property.mapper;

import com.homeflex.features.property.dto.response.TopPropertyDto;
import com.homeflex.features.property.domain.entity.Property;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    TopPropertyDto toTopPropertyDto(Property property);

    List<TopPropertyDto> toTopPropertyDtoList(List<Property> properties);
}
