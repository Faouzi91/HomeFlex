import 'package:freezed_annotation/freezed_annotation.dart';

part 'property_video_dto.freezed.dart';
part 'property_video_dto.g.dart';

@freezed
abstract class PropertyVideoDto with _$PropertyVideoDto {
  const factory PropertyVideoDto({
    required String id,
    required String videoUrl,
    String? thumbnailUrl,
    int? durationSeconds,
  }) = _PropertyVideoDto;

  factory PropertyVideoDto.fromJson(Map<String, dynamic> json) => _$PropertyVideoDtoFromJson(json);
}
