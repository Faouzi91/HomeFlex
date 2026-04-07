// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'review_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ReviewDto _$ReviewDtoFromJson(Map<String, dynamic> json) => _ReviewDto(
  id: json['id'] as String,
  propertyId: json['propertyId'] as String,
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
      'propertyId': instance.propertyId,
      'reviewer': instance.reviewer,
      'rating': instance.rating,
      'comment': instance.comment,
      'createdAt': instance.createdAt,
    };
