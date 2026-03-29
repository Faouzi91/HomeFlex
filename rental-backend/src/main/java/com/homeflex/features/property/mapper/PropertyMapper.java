package com.homeflex.features.property.mapper;

import com.homeflex.core.mapper.UserMapper;

import com.homeflex.features.property.dto.response.AmenityDto;
import com.homeflex.features.property.dto.response.PropertyDto;
import com.homeflex.features.property.dto.response.PropertyImageDto;
import com.homeflex.features.property.dto.response.PropertyVideoDto;
import com.homeflex.features.property.domain.entity.Amenity;
import com.homeflex.features.property.domain.entity.Property;
import com.homeflex.features.property.domain.entity.PropertyImage;
import com.homeflex.features.property.domain.entity.PropertyVideo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface PropertyMapper {

    @Mapping(target = "propertyType", expression = "java(property.getPropertyType() != null ? property.getPropertyType().name() : null)")
    @Mapping(target = "listingType", expression = "java(property.getListingType() != null ? property.getListingType().name() : null)")
    @Mapping(target = "status", expression = "java(property.getStatus() != null ? property.getStatus().name() : null)")
    @Mapping(target = "images", expression = "java(toImageList(property.getImages()))")
    @Mapping(target = "videos", expression = "java(toVideoList(property.getVideos()))")
    @Mapping(target = "amenities", expression = "java(toAmenityList(property.getAmenities()))")
    @Mapping(target = "landlord", source = "landlord")
    PropertyDto toDto(Property property);

    List<PropertyDto> toDto(List<Property> properties);

    @Mapping(target = "category", expression = "java(amenity.getCategory() != null ? amenity.getCategory().name() : null)")
    AmenityDto toAmenityDto(Amenity amenity);

    PropertyImageDto toImageDto(PropertyImage image);

    PropertyVideoDto toVideoDto(PropertyVideo video);

    default List<PropertyImageDto> toImageList(Set<PropertyImage> images) {
        return images == null ? List.of() : images.stream()
                .sorted((a, b) -> Integer.compare(
                        a.getDisplayOrder() == null ? 0 : a.getDisplayOrder(),
                        b.getDisplayOrder() == null ? 0 : b.getDisplayOrder()))
                .map(this::toImageDto)
                .toList();
    }

    default List<PropertyVideoDto> toVideoList(Set<PropertyVideo> videos) {
        return videos == null ? List.of() : videos.stream().map(this::toVideoDto).toList();
    }

    default List<AmenityDto> toAmenityList(Set<Amenity> amenities) {
        return amenities == null ? List.of() : amenities.stream().map(this::toAmenityDto).toList();
    }
}
