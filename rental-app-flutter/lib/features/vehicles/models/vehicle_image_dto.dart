import 'package:freezed_annotation/freezed_annotation.dart';

part 'vehicle_image_dto.freezed.dart';
part 'vehicle_image_dto.g.dart';

@freezed
abstract class VehicleImageDto with _$VehicleImageDto {
  const factory VehicleImageDto({
    required String id,
    required String imageUrl,
    required int displayOrder,
    required bool isPrimary,
  }) = _VehicleImageDto;

  factory VehicleImageDto.fromJson(Map<String, dynamic> json) => _$VehicleImageDtoFromJson(json);
}
