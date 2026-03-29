package com.homeflex.features.vehicle.mapper;

import com.homeflex.features.vehicle.domain.entity.Vehicle;
import com.homeflex.features.vehicle.dto.request.VehicleCreateRequest;
import com.homeflex.features.vehicle.dto.response.VehicleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "transmission", expression = "java(vehicle.getTransmission().name())")
    @Mapping(target = "fuelType", expression = "java(vehicle.getFuelType().name())")
    @Mapping(target = "status", expression = "java(vehicle.getStatus().name())")
    VehicleResponse toResponse(Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vehicle toEntity(VehicleCreateRequest request);
}
