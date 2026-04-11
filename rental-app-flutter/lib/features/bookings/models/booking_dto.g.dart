// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'booking_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_BookingDto _$BookingDtoFromJson(Map<String, dynamic> json) => _BookingDto(
  id: json['id'] as String,
  property: json['property'] == null
      ? null
      : PropertyDto.fromJson(json['property'] as Map<String, dynamic>),
  tenant: json['tenant'] == null
      ? null
      : UserDto.fromJson(json['tenant'] as Map<String, dynamic>),
  bookingType: json['bookingType'] as String,
  requestedDate: json['requestedDate'] as String?,
  startDate: json['startDate'] as String?,
  endDate: json['endDate'] as String?,
  status: json['status'] as String,
  message: json['message'] as String?,
  numberOfOccupants: (json['numberOfOccupants'] as num?)?.toInt(),
  totalPrice: (json['totalPrice'] as num?)?.toDouble(),
  platformFee: (json['platformFee'] as num?)?.toDouble(),
  stripePaymentIntentId: json['stripePaymentIntentId'] as String?,
  paymentConfirmedAt: json['paymentConfirmedAt'] as String?,
  escrowReleasedAt: json['escrowReleasedAt'] as String?,
  landlordResponse: json['landlordResponse'] as String?,
  proposedStartDate: json['proposedStartDate'] as String?,
  proposedEndDate: json['proposedEndDate'] as String?,
  modificationReason: json['modificationReason'] as String?,
  respondedAt: json['respondedAt'] as String?,
  createdAt: json['createdAt'] as String,
);

Map<String, dynamic> _$BookingDtoToJson(_BookingDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'property': instance.property,
      'tenant': instance.tenant,
      'bookingType': instance.bookingType,
      'requestedDate': instance.requestedDate,
      'startDate': instance.startDate,
      'endDate': instance.endDate,
      'status': instance.status,
      'message': instance.message,
      'numberOfOccupants': instance.numberOfOccupants,
      'totalPrice': instance.totalPrice,
      'platformFee': instance.platformFee,
      'stripePaymentIntentId': instance.stripePaymentIntentId,
      'paymentConfirmedAt': instance.paymentConfirmedAt,
      'escrowReleasedAt': instance.escrowReleasedAt,
      'landlordResponse': instance.landlordResponse,
      'proposedStartDate': instance.proposedStartDate,
      'proposedEndDate': instance.proposedEndDate,
      'modificationReason': instance.modificationReason,
      'respondedAt': instance.respondedAt,
      'createdAt': instance.createdAt,
    };
