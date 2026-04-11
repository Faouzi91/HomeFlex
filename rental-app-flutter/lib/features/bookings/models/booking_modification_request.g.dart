// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'booking_modification_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_BookingModificationRequest _$BookingModificationRequestFromJson(
  Map<String, dynamic> json,
) => _BookingModificationRequest(
  startDate: json['startDate'] as String,
  endDate: json['endDate'] as String,
  reason: json['reason'] as String?,
);

Map<String, dynamic> _$BookingModificationRequestToJson(
  _BookingModificationRequest instance,
) => <String, dynamic>{
  'startDate': instance.startDate,
  'endDate': instance.endDate,
  'reason': instance.reason,
};
