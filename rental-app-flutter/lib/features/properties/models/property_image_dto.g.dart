// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'property_image_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PropertyImageDto _$PropertyImageDtoFromJson(Map<String, dynamic> json) =>
    _PropertyImageDto(
      id: json['id'] as String,
      imageUrl: json['imageUrl'] as String,
      thumbnailUrl: json['thumbnailUrl'] as String?,
      displayOrder: (json['displayOrder'] as num?)?.toInt(),
      isPrimary: json['isPrimary'] as bool?,
    );

Map<String, dynamic> _$PropertyImageDtoToJson(_PropertyImageDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'imageUrl': instance.imageUrl,
      'thumbnailUrl': instance.thumbnailUrl,
      'displayOrder': instance.displayOrder,
      'isPrimary': instance.isPrimary,
    };
