// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'amenity_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AmenityDto _$AmenityDtoFromJson(Map<String, dynamic> json) => _AmenityDto(
  id: json['id'] as String,
  name: json['name'] as String,
  nameFr: json['nameFr'] as String,
  icon: json['icon'] as String?,
  category: json['category'] as String,
);

Map<String, dynamic> _$AmenityDtoToJson(_AmenityDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'nameFr': instance.nameFr,
      'icon': instance.icon,
      'category': instance.category,
    };
