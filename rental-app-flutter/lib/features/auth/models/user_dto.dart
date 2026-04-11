import 'package:freezed_annotation/freezed_annotation.dart';

part 'user_dto.freezed.dart';
part 'user_dto.g.dart';

@freezed
abstract class UserDto with _$UserDto {
  const factory UserDto({
    required String id,
    required String email,
    required String firstName,
    required String lastName,
    String? phoneNumber,
    String? profilePictureUrl,
    required String role,
    required bool isActive,
    required bool isVerified,
    String? languagePreference,
    String? agencyId,
    String? agencyRole,
    @Default(5.0) double trustScore,
    required String createdAt,
  }) = _UserDto;

  factory UserDto.fromJson(Map<String, dynamic> json) => _$UserDtoFromJson(json);
}
