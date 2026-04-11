// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'dispute_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_DisputeDto _$DisputeDtoFromJson(Map<String, dynamic> json) => _DisputeDto(
  id: json['id'] as String,
  bookingId: json['bookingId'] as String,
  initiatorId: json['initiatorId'] as String,
  reason: json['reason'] as String,
  description: json['description'] as String?,
  status: json['status'] as String,
  resolutionNotes: json['resolutionNotes'] as String?,
  resolvedAt: json['resolvedAt'] as String?,
  resolvedById: json['resolvedById'] as String?,
  createdAt: json['createdAt'] as String,
  updatedAt: json['updatedAt'] as String,
);

Map<String, dynamic> _$DisputeDtoToJson(_DisputeDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'bookingId': instance.bookingId,
      'initiatorId': instance.initiatorId,
      'reason': instance.reason,
      'description': instance.description,
      'status': instance.status,
      'resolutionNotes': instance.resolutionNotes,
      'resolvedAt': instance.resolvedAt,
      'resolvedById': instance.resolvedById,
      'createdAt': instance.createdAt,
      'updatedAt': instance.updatedAt,
    };
