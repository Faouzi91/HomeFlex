import 'package:freezed_annotation/freezed_annotation.dart';
import 'vehicle_image_dto.dart';

part 'vehicle_response.freezed.dart';
part 'vehicle_response.g.dart';

@freezed
abstract class VehicleResponse with _$VehicleResponse {
  const factory VehicleResponse({
    required String id,
    required String ownerId,
    required String brand,
    required String model,
    required int year,
    required String transmission,
    required String fuelType,
    required double dailyPrice,
    required String currency,
    required String status,
    String? description,
    int? mileage,
    int? seats,
    String? color,
    String? licensePlate,
    String? pickupCity,
    String? pickupAddress,
    required int viewCount,
    List<VehicleImageDto>? images,
    required String createdAt,
    required String updatedAt,
  }) = _VehicleResponse;

  factory VehicleResponse.fromJson(Map<String, dynamic> json) => _$VehicleResponseFromJson(json);
}
