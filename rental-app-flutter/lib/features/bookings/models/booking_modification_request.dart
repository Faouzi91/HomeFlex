import 'package:freezed_annotation/freezed_annotation.dart';

part 'booking_modification_request.freezed.dart';
part 'booking_modification_request.g.dart';

@freezed
abstract class BookingModificationRequest with _$BookingModificationRequest {
  const factory BookingModificationRequest({
    required String startDate,
    required String endDate,
    String? reason,
  }) = _BookingModificationRequest;

  factory BookingModificationRequest.fromJson(Map<String, dynamic> json) =>
      _$BookingModificationRequestFromJson(json);
}
