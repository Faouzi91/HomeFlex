import 'package:freezed_annotation/freezed_annotation.dart';

part 'agency_dto.freezed.dart';
part 'agency_dto.g.dart';

@freezed
abstract class AgencyDto with _$AgencyDto {
  const factory AgencyDto({
    required String id,
    required String name,
    String? description,
    String? logoUrl,
    String? websiteUrl,
    required String email,
    String? phoneNumber,
    String? address,
    required bool isVerified,
    String? customDomain,
    required String themePrimaryColor,
    required String createdAt,
  }) = _AgencyDto;

  factory AgencyDto.fromJson(Map<String, dynamic> json) => _$AgencyDtoFromJson(json);
}
