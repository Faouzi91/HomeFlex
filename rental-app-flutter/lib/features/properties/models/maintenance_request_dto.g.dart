// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'maintenance_request_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_MaintenanceRequestDto _$MaintenanceRequestDtoFromJson(
  Map<String, dynamic> json,
) => _MaintenanceRequestDto(
  id: json['id'] as String,
  propertyId: json['propertyId'] as String,
  propertyTitle: json['propertyTitle'] as String,
  tenantId: json['tenantId'] as String,
  tenantName: json['tenantName'] as String,
  title: json['title'] as String,
  description: json['description'] as String,
  category: $enumDecode(_$MaintenanceCategoryEnumMap, json['category']),
  priority: $enumDecode(_$MaintenancePriorityEnumMap, json['priority']),
  status: $enumDecode(_$MaintenanceStatusEnumMap, json['status']),
  resolutionNotes: json['resolutionNotes'] as String?,
  resolvedAt: json['resolvedAt'] as String?,
  imageUrls: (json['imageUrls'] as List<dynamic>?)
      ?.map((e) => e as String)
      .toList(),
  createdAt: json['createdAt'] as String,
  updatedAt: json['updatedAt'] as String,
);

Map<String, dynamic> _$MaintenanceRequestDtoToJson(
  _MaintenanceRequestDto instance,
) => <String, dynamic>{
  'id': instance.id,
  'propertyId': instance.propertyId,
  'propertyTitle': instance.propertyTitle,
  'tenantId': instance.tenantId,
  'tenantName': instance.tenantName,
  'title': instance.title,
  'description': instance.description,
  'category': _$MaintenanceCategoryEnumMap[instance.category]!,
  'priority': _$MaintenancePriorityEnumMap[instance.priority]!,
  'status': _$MaintenanceStatusEnumMap[instance.status]!,
  'resolutionNotes': instance.resolutionNotes,
  'resolvedAt': instance.resolvedAt,
  'imageUrls': instance.imageUrls,
  'createdAt': instance.createdAt,
  'updatedAt': instance.updatedAt,
};

const _$MaintenanceCategoryEnumMap = {
  MaintenanceCategory.PLUMBING: 'PLUMBING',
  MaintenanceCategory.ELECTRICAL: 'ELECTRICAL',
  MaintenanceCategory.APPLIANCE: 'APPLIANCE',
  MaintenanceCategory.STRUCTURAL: 'STRUCTURAL',
  MaintenanceCategory.OTHER: 'OTHER',
};

const _$MaintenancePriorityEnumMap = {
  MaintenancePriority.LOW: 'LOW',
  MaintenancePriority.MEDIUM: 'MEDIUM',
  MaintenancePriority.HIGH: 'HIGH',
  MaintenancePriority.URGENT: 'URGENT',
};

const _$MaintenanceStatusEnumMap = {
  MaintenanceStatus.REPORTED: 'REPORTED',
  MaintenanceStatus.IN_PROGRESS: 'IN_PROGRESS',
  MaintenanceStatus.RESOLVED: 'RESOLVED',
  MaintenanceStatus.CANCELLED: 'CANCELLED',
};

_MaintenanceRequestCreateRequest _$MaintenanceRequestCreateRequestFromJson(
  Map<String, dynamic> json,
) => _MaintenanceRequestCreateRequest(
  propertyId: json['propertyId'] as String,
  title: json['title'] as String,
  description: json['description'] as String,
  category: $enumDecode(_$MaintenanceCategoryEnumMap, json['category']),
  priority: $enumDecode(_$MaintenancePriorityEnumMap, json['priority']),
);

Map<String, dynamic> _$MaintenanceRequestCreateRequestToJson(
  _MaintenanceRequestCreateRequest instance,
) => <String, dynamic>{
  'propertyId': instance.propertyId,
  'title': instance.title,
  'description': instance.description,
  'category': _$MaintenanceCategoryEnumMap[instance.category]!,
  'priority': _$MaintenancePriorityEnumMap[instance.priority]!,
};

_MaintenanceStatusUpdateRequest _$MaintenanceStatusUpdateRequestFromJson(
  Map<String, dynamic> json,
) => _MaintenanceStatusUpdateRequest(
  status: $enumDecode(_$MaintenanceStatusEnumMap, json['status']),
  resolutionNotes: json['resolutionNotes'] as String?,
);

Map<String, dynamic> _$MaintenanceStatusUpdateRequestToJson(
  _MaintenanceStatusUpdateRequest instance,
) => <String, dynamic>{
  'status': _$MaintenanceStatusEnumMap[instance.status]!,
  'resolutionNotes': instance.resolutionNotes,
};
