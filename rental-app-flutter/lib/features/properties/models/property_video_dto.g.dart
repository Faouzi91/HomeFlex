// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'property_video_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PropertyVideoDto _$PropertyVideoDtoFromJson(Map<String, dynamic> json) =>
    _PropertyVideoDto(
      id: json['id'] as String,
      videoUrl: json['videoUrl'] as String,
      thumbnailUrl: json['thumbnailUrl'] as String?,
      durationSeconds: (json['durationSeconds'] as num?)?.toInt(),
    );

Map<String, dynamic> _$PropertyVideoDtoToJson(_PropertyVideoDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'videoUrl': instance.videoUrl,
      'thumbnailUrl': instance.thumbnailUrl,
      'durationSeconds': instance.durationSeconds,
    };
