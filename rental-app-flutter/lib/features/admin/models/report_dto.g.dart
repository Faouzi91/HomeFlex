// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'report_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ReportDto _$ReportDtoFromJson(Map<String, dynamic> json) => _ReportDto(
  id: json['id'] as String,
  reporter: json['reporter'] == null
      ? null
      : UserDto.fromJson(json['reporter'] as Map<String, dynamic>),
  propertyId: json['propertyId'] as String?,
  reason: json['reason'] as String?,
  description: json['description'] as String?,
  status: json['status'] as String,
  resolvedAt: json['resolvedAt'] as String?,
  createdAt: json['createdAt'] as String,
);

Map<String, dynamic> _$ReportDtoToJson(_ReportDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'reporter': instance.reporter,
      'propertyId': instance.propertyId,
      'reason': instance.reason,
      'description': instance.description,
      'status': instance.status,
      'resolvedAt': instance.resolvedAt,
      'createdAt': instance.createdAt,
    };
