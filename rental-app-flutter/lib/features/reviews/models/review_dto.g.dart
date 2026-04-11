// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'review_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ReviewDto _$ReviewDtoFromJson(Map<String, dynamic> json) => _ReviewDto(
  id: json['id'] as String,
  type: $enumDecode(_$ReviewTypeEnumMap, json['type']),
  propertyId: json['propertyId'] as String?,
  targetUser: json['targetUser'] == null
      ? null
      : UserDto.fromJson(json['targetUser'] as Map<String, dynamic>),
  reviewer: json['reviewer'] == null
      ? null
      : UserDto.fromJson(json['reviewer'] as Map<String, dynamic>),
  rating: (json['rating'] as num).toInt(),
  comment: json['comment'] as String?,
  createdAt: json['createdAt'] as String,
);

Map<String, dynamic> _$ReviewDtoToJson(_ReviewDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'type': _$ReviewTypeEnumMap[instance.type]!,
      'propertyId': instance.propertyId,
      'targetUser': instance.targetUser,
      'reviewer': instance.reviewer,
      'rating': instance.rating,
      'comment': instance.comment,
      'createdAt': instance.createdAt,
    };

const _$ReviewTypeEnumMap = {
  ReviewType.PROPERTY: 'PROPERTY',
  ReviewType.TENANT: 'TENANT',
};

_ReviewCreateRequest _$ReviewCreateRequestFromJson(Map<String, dynamic> json) =>
    _ReviewCreateRequest(
      propertyId: json['propertyId'] as String?,
      targetUserId: json['targetUserId'] as String?,
      rating: (json['rating'] as num).toInt(),
      comment: json['comment'] as String?,
    );

Map<String, dynamic> _$ReviewCreateRequestToJson(
  _ReviewCreateRequest instance,
) => <String, dynamic>{
  'propertyId': instance.propertyId,
  'targetUserId': instance.targetUserId,
  'rating': instance.rating,
  'comment': instance.comment,
};
