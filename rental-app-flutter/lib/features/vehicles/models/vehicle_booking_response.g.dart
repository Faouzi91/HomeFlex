// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'vehicle_booking_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_VehicleBookingResponse _$VehicleBookingResponseFromJson(
  Map<String, dynamic> json,
) => _VehicleBookingResponse(
  id: json['id'] as String,
  vehicleId: json['vehicleId'] as String,
  tenantId: json['tenantId'] as String,
  startDate: json['startDate'] as String,
  endDate: json['endDate'] as String,
  totalPrice: (json['totalPrice'] as num?)?.toDouble(),
  currency: json['currency'] as String,
  status: json['status'] as String,
  platformFee: (json['platformFee'] as num?)?.toDouble(),
  message: json['message'] as String?,
  createdAt: json['createdAt'] as String,
);

Map<String, dynamic> _$VehicleBookingResponseToJson(
  _VehicleBookingResponse instance,
) => <String, dynamic>{
  'id': instance.id,
  'vehicleId': instance.vehicleId,
  'tenantId': instance.tenantId,
  'startDate': instance.startDate,
  'endDate': instance.endDate,
  'totalPrice': instance.totalPrice,
  'currency': instance.currency,
  'status': instance.status,
  'platformFee': instance.platformFee,
  'message': instance.message,
  'createdAt': instance.createdAt,
};
