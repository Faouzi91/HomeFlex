// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'vehicle_image_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_VehicleImageDto _$VehicleImageDtoFromJson(Map<String, dynamic> json) =>
    _VehicleImageDto(
      id: json['id'] as String,
      imageUrl: json['imageUrl'] as String,
      displayOrder: (json['displayOrder'] as num).toInt(),
      isPrimary: json['isPrimary'] as bool,
    );

Map<String, dynamic> _$VehicleImageDtoToJson(_VehicleImageDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'imageUrl': instance.imageUrl,
      'displayOrder': instance.displayOrder,
      'isPrimary': instance.isPrimary,
    };
