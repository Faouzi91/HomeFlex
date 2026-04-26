package com.homeflex.features.property.api.v1;

import com.homeflex.core.dto.common.ApiListResponse;
import com.homeflex.features.property.dto.request.RoomTypeCreateRequest;
import com.homeflex.features.property.dto.response.RoomTypeDto;
import com.homeflex.features.property.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties/{propertyId}/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @GetMapping
    public ResponseEntity<ApiListResponse<RoomTypeDto>> listRoomTypes(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(new ApiListResponse<>(roomTypeService.getRoomTypes(propertyId)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PROPERTY_UPDATE')")
    public ResponseEntity<RoomTypeDto> createRoomType(
            @PathVariable UUID propertyId,
            @Valid @RequestBody RoomTypeCreateRequest request,
            Authentication auth) {
        UUID ownerId = UUID.fromString(auth.getName());
        RoomTypeDto created = roomTypeService.createRoomType(propertyId, request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{roomTypeId}")
    @PreAuthorize("hasAuthority('PROPERTY_UPDATE')")
    public ResponseEntity<RoomTypeDto> updateRoomType(
            @PathVariable UUID propertyId,
            @PathVariable UUID roomTypeId,
            @Valid @RequestBody RoomTypeCreateRequest request,
            Authentication auth) {
        UUID ownerId = UUID.fromString(auth.getName());
        return ResponseEntity.ok(roomTypeService.updateRoomType(propertyId, roomTypeId, request, ownerId));
    }

    @DeleteMapping("/{roomTypeId}")
    @PreAuthorize("hasAuthority('PROPERTY_UPDATE')")
    public ResponseEntity<Void> deleteRoomType(
            @PathVariable UUID propertyId,
            @PathVariable UUID roomTypeId,
            Authentication auth) {
        UUID ownerId = UUID.fromString(auth.getName());
        roomTypeService.deleteRoomType(propertyId, roomTypeId, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roomTypeId}/images")
    @PreAuthorize("hasAuthority('PROPERTY_UPDATE')")
    public ResponseEntity<RoomTypeDto> uploadImages(
            @PathVariable UUID propertyId,
            @PathVariable UUID roomTypeId,
            @RequestPart("images") List<MultipartFile> images,
            Authentication auth) {
        UUID ownerId = UUID.fromString(auth.getName());
        return ResponseEntity.ok(roomTypeService.addImages(propertyId, roomTypeId, images, ownerId));
    }
}
