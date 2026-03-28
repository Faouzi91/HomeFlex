package com.realestate.rental.mapper;

import com.realestate.rental.dto.response.FavoriteDto;
import com.realestate.rental.domain.entity.Favorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PropertyMapper.class})
public interface FavoriteMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "propertyId", source = "property.id")
    FavoriteDto toDto(Favorite favorite);
}
