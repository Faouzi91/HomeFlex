import 'package:freezed_annotation/freezed_annotation.dart';

part 'property_image_dto.freezed.dart';
part 'property_image_dto.g.dart';

@freezed
abstract class PropertyImageDto with _$PropertyImageDto {
  const factory PropertyImageDto({
    required String id,
    required String imageUrl,
    String? thumbnailUrl,
    int? displayOrder,
    bool? isPrimary,
  }) = _PropertyImageDto;

  factory PropertyImageDto.fromJson(Map<String, dynamic> json) => _$PropertyImageDtoFromJson(json);
}
