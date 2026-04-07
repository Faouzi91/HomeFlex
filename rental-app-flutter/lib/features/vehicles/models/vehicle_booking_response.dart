import 'package:freezed_annotation/freezed_annotation.dart';

part 'vehicle_booking_response.freezed.dart';
part 'vehicle_booking_response.g.dart';

@freezed
abstract class VehicleBookingResponse with _$VehicleBookingResponse {
  const factory VehicleBookingResponse({
    required String id,
    required String vehicleId,
    required String tenantId,
    required String startDate,
    required String endDate,
    double? totalPrice,
    required String currency,
    required String status,
    double? platformFee,
    String? message,
    required String createdAt,
  }) = _VehicleBookingResponse;

  factory VehicleBookingResponse.fromJson(Map<String, dynamic> json) =>
      _$VehicleBookingResponseFromJson(json);
}
