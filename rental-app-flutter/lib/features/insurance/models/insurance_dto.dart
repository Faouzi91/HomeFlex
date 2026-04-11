import 'package:freezed_annotation/freezed_annotation.dart';
import '../../auth/models/user_dto.dart';
import '../../bookings/models/booking_dto.dart';

part 'insurance_dto.freezed.dart';
part 'insurance_dto.g.dart';

@freezed
abstract class InsurancePlanDto with _$InsurancePlanDto {
  const factory InsurancePlanDto({
    required String id,
    required String providerName,
    required String name,
    required String type,
    required String description,
    required String coverageDetails,
    required double dailyPremium,
    double? maxCoverageAmount,
  }) = _InsurancePlanDto;

  factory InsurancePlanDto.fromJson(Map<String, dynamic> json) => _$InsurancePlanDtoFromJson(json);
}

@freezed
abstract class InsurancePolicyDto with _$InsurancePolicyDto {
  const factory InsurancePolicyDto({
    required String id,
    required InsurancePlanDto plan,
    required UserDto user,
    BookingDto? booking,
    required String policyNumber,
    required String status,
    required String startDate,
    required String endDate,
    required double totalPremium,
    String? certificateUrl,
    required String createdAt,
  }) = _InsurancePolicyDto;

  factory InsurancePolicyDto.fromJson(Map<String, dynamic> json) => _$InsurancePolicyDtoFromJson(json);
}
