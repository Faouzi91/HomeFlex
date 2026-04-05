package com.homeflex.features.vehicle.mapper;

import com.homeflex.features.vehicle.domain.entity.ConditionReport;
import com.homeflex.features.vehicle.domain.entity.Vehicle;
import com.homeflex.features.vehicle.domain.entity.VehicleBooking;
import com.homeflex.features.vehicle.domain.entity.VehicleImage;
import com.homeflex.features.vehicle.dto.request.VehicleCreateRequest;
import com.homeflex.features.vehicle.dto.response.ConditionReportResponse;
import com.homeflex.features.vehicle.dto.response.VehicleBookingResponse;
import com.homeflex.features.vehicle.dto.response.VehicleImageDto;
import com.homeflex.features.vehicle.dto.response.VehicleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "transmission", expression = "java(vehicle.getTransmission().name())")
    @Mapping(target = "fuelType", expression = "java(vehicle.getFuelType().name())")
    @Mapping(target = "status", expression = "java(vehicle.getStatus().name())")
    @Mapping(target = "images", expression = "java(toImageList(vehicle.getImages()))")
    VehicleResponse toResponse(Vehicle vehicle);

    @Mapping(target = "isPrimary", source = "primary")
    VehicleImageDto toImageDto(VehicleImage image);

    ConditionReportResponse toConditionReportResponse(ConditionReport report);

    VehicleBookingResponse toBookingResponse(VehicleBooking booking);

    default List<VehicleBookingResponse> toBookingResponseList(List<VehicleBooking> bookings) {
        return bookings == null ? List.of() : bookings.stream()
                .map(this::toBookingResponse)
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "images", ignore = true)
    Vehicle toEntity(VehicleCreateRequest request);

    default List<VehicleImageDto> toImageList(Set<VehicleImage> images) {
        return images == null ? List.of() : images.stream()
                .sorted(Comparator.comparingInt(VehicleImage::getDisplayOrder))
                .map(this::toImageDto)
                .toList();
    }
}
