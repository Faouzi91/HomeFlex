// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_UserDto _$UserDtoFromJson(Map<String, dynamic> json) => _UserDto(
  id: json['id'] as String,
  email: json['email'] as String,
  firstName: json['firstName'] as String,
  lastName: json['lastName'] as String,
  phoneNumber: json['phoneNumber'] as String?,
  profilePictureUrl: json['profilePictureUrl'] as String?,
  role: json['role'] as String,
  isActive: json['isActive'] as bool,
  isVerified: json['isVerified'] as bool,
  languagePreference: json['languagePreference'] as String?,
  agencyId: json['agencyId'] as String?,
  agencyRole: json['agencyRole'] as String?,
  trustScore: (json['trustScore'] as num?)?.toDouble() ?? 5.0,
  createdAt: json['createdAt'] as String,
);

Map<String, dynamic> _$UserDtoToJson(_UserDto instance) => <String, dynamic>{
  'id': instance.id,
  'email': instance.email,
  'firstName': instance.firstName,
  'lastName': instance.lastName,
  'phoneNumber': instance.phoneNumber,
  'profilePictureUrl': instance.profilePictureUrl,
  'role': instance.role,
  'isActive': instance.isActive,
  'isVerified': instance.isVerified,
  'languagePreference': instance.languagePreference,
  'agencyId': instance.agencyId,
  'agencyRole': instance.agencyRole,
  'trustScore': instance.trustScore,
  'createdAt': instance.createdAt,
};
