package com.homeflex.features.property.mapper;

import com.homeflex.core.mapper.UserMapper;

import com.homeflex.features.property.dto.response.ReportDto;
import com.homeflex.features.property.domain.entity.ReportedListing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ReportMapper {

    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "propertyTitle", source = "property.title")
    ReportDto toDto(ReportedListing reportedListing);
}
