package com.homeflex.features.property.mapper;

import com.homeflex.core.mapper.UserMapper;

import com.homeflex.features.property.dto.response.ReviewDto;
import com.homeflex.features.property.domain.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public abstract class ReviewMapper {

    @org.springframework.beans.factory.annotation.Autowired
    protected UserMapper userMapper;

    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "reviewer", expression = "java(userMapper.toPublicDto(review.getReviewer()))")
    @Mapping(target = "targetUser", expression = "java(userMapper.toPublicDto(review.getTargetUser()))")
    public abstract ReviewDto toDto(Review review);
}
