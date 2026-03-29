package com.homeflex.features.property.mapper;

import com.homeflex.features.property.dto.response.FavoriteDto;
import com.homeflex.features.property.domain.entity.Favorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PropertyMapper.class})
public interface FavoriteMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "propertyId", source = "property.id")
    FavoriteDto toDto(Favorite favorite);
}
