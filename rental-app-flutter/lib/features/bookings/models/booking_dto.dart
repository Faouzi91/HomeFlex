import 'package:freezed_annotation/freezed_annotation.dart';
import '../../properties/models/property_dto.dart';
import '../../auth/models/user_dto.dart';

part 'booking_dto.freezed.dart';
part 'booking_dto.g.dart';

@freezed
abstract class BookingDto with _$BookingDto {
  const factory BookingDto({
    required String id,
    PropertyDto? property,
    UserDto? tenant,
    required String bookingType,
    String? requestedDate,
    String? startDate,
    String? endDate,
    required String status,
    String? message,
    int? numberOfOccupants,
    double? totalPrice,
    double? platformFee,
    String? stripePaymentIntentId,
    String? paymentConfirmedAt,
    String? escrowReleasedAt,
    String? landlordResponse,
    String? respondedAt,
    required String createdAt,
  }) = _BookingDto;

  factory BookingDto.fromJson(Map<String, dynamic> json) => _$BookingDtoFromJson(json);
}
