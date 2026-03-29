package com.homeflex.features.property.mapper;

import com.homeflex.core.mapper.UserMapper;

import com.homeflex.features.property.dto.response.ReviewDto;
import com.homeflex.features.property.domain.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ReviewMapper {

    @Mapping(target = "propertyId", source = "property.id")
    ReviewDto toDto(Review review);
}
