// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'agency_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AgencyDto _$AgencyDtoFromJson(Map<String, dynamic> json) => _AgencyDto(
  id: json['id'] as String,
  name: json['name'] as String,
  description: json['description'] as String?,
  logoUrl: json['logoUrl'] as String?,
  websiteUrl: json['websiteUrl'] as String?,
  email: json['email'] as String,
  phoneNumber: json['phoneNumber'] as String?,
  address: json['address'] as String?,
  isVerified: json['isVerified'] as bool,
  customDomain: json['customDomain'] as String?,
  themePrimaryColor: json['themePrimaryColor'] as String,
  createdAt: json['createdAt'] as String,
);

Map<String, dynamic> _$AgencyDtoToJson(_AgencyDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'description': instance.description,
      'logoUrl': instance.logoUrl,
      'websiteUrl': instance.websiteUrl,
      'email': instance.email,
      'phoneNumber': instance.phoneNumber,
      'address': instance.address,
      'isVerified': instance.isVerified,
      'customDomain': instance.customDomain,
      'themePrimaryColor': instance.themePrimaryColor,
      'createdAt': instance.createdAt,
    };
