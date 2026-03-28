package com.realestate.rental.mapper;

import com.realestate.rental.dto.response.ReportDto;
import com.realestate.rental.domain.entity.ReportedListing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ReportMapper {

    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "propertyTitle", source = "property.title")
    ReportDto toDto(ReportedListing reportedListing);
}
