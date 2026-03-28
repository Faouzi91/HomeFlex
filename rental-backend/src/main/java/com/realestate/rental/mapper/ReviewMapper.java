package com.realestate.rental.mapper;

import com.realestate.rental.dto.response.ReviewDto;
import com.realestate.rental.domain.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ReviewMapper {

    @Mapping(target = "propertyId", source = "property.id")
    ReviewDto toDto(Review review);
}
