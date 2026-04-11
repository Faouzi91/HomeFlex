import 'package:freezed_annotation/freezed_annotation.dart';

part 'maintenance_request_dto.freezed.dart';
part 'maintenance_request_dto.g.dart';

enum MaintenanceCategory {
  PLUMBING,
  ELECTRICAL,
  APPLIANCE,
  STRUCTURAL,
  OTHER,
}

enum MaintenancePriority {
  LOW,
  MEDIUM,
  HIGH,
  URGENT,
}

enum MaintenanceStatus {
  REPORTED,
  IN_PROGRESS,
  RESOLVED,
  CANCELLED,
}

@freezed
abstract class MaintenanceRequestDto with _$MaintenanceRequestDto {
  const factory MaintenanceRequestDto({
    required String id,
    required String propertyId,
    required String propertyTitle,
    required String tenantId,
    required String tenantName,
    required String title,
    required String description,
    required MaintenanceCategory category,
    required MaintenancePriority priority,
    required MaintenanceStatus status,
    String? resolutionNotes,
    String? resolvedAt,
    List<String>? imageUrls,
    required String createdAt,
    required String updatedAt,
  }) = _MaintenanceRequestDto;

  factory MaintenanceRequestDto.fromJson(Map<String, dynamic> json) =>
      _$MaintenanceRequestDtoFromJson(json);
}

@freezed
abstract class MaintenanceRequestCreateRequest with _$MaintenanceRequestCreateRequest {
  const factory MaintenanceRequestCreateRequest({
    required String propertyId,
    required String title,
    required String description,
    required MaintenanceCategory category,
    required MaintenancePriority priority,
  }) = _MaintenanceRequestCreateRequest;

  factory MaintenanceRequestCreateRequest.fromJson(Map<String, dynamic> json) =>
      _$MaintenanceRequestCreateRequestFromJson(json);
}

@freezed
abstract class MaintenanceStatusUpdateRequest with _$MaintenanceStatusUpdateRequest {
  const factory MaintenanceStatusUpdateRequest({
    required MaintenanceStatus status,
    String? resolutionNotes,
  }) = _MaintenanceStatusUpdateRequest;

  factory MaintenanceStatusUpdateRequest.fromJson(Map<String, dynamic> json) =>
      _$MaintenanceStatusUpdateRequestFromJson(json);
}
