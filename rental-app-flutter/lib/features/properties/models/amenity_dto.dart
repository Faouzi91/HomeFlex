import 'package:freezed_annotation/freezed_annotation.dart';

part 'amenity_dto.freezed.dart';
part 'amenity_dto.g.dart';

@freezed
abstract class AmenityDto with _$AmenityDto {
  const factory AmenityDto({
    required String id,
    required String name,
    required String nameFr,
    String? icon,
    required String category,
  }) = _AmenityDto;

  factory AmenityDto.fromJson(Map<String, dynamic> json) => _$AmenityDtoFromJson(json);
}
