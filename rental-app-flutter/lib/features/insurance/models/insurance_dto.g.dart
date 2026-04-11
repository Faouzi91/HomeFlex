// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurance_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_InsurancePlanDto _$InsurancePlanDtoFromJson(Map<String, dynamic> json) =>
    _InsurancePlanDto(
      id: json['id'] as String,
      providerName: json['providerName'] as String,
      name: json['name'] as String,
      type: json['type'] as String,
      description: json['description'] as String,
      coverageDetails: json['coverageDetails'] as String,
      dailyPremium: (json['dailyPremium'] as num).toDouble(),
      maxCoverageAmount: (json['maxCoverageAmount'] as num?)?.toDouble(),
    );

Map<String, dynamic> _$InsurancePlanDtoToJson(_InsurancePlanDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'providerName': instance.providerName,
      'name': instance.name,
      'type': instance.type,
      'description': instance.description,
      'coverageDetails': instance.coverageDetails,
      'dailyPremium': instance.dailyPremium,
      'maxCoverageAmount': instance.maxCoverageAmount,
    };

_InsurancePolicyDto _$InsurancePolicyDtoFromJson(Map<String, dynamic> json) =>
    _InsurancePolicyDto(
      id: json['id'] as String,
      plan: InsurancePlanDto.fromJson(json['plan'] as Map<String, dynamic>),
      user: UserDto.fromJson(json['user'] as Map<String, dynamic>),
      booking: json['booking'] == null
          ? null
          : BookingDto.fromJson(json['booking'] as Map<String, dynamic>),
      policyNumber: json['policyNumber'] as String,
      status: json['status'] as String,
      startDate: json['startDate'] as String,
      endDate: json['endDate'] as String,
      totalPremium: (json['totalPremium'] as num).toDouble(),
      certificateUrl: json['certificateUrl'] as String?,
      createdAt: json['createdAt'] as String,
    );

Map<String, dynamic> _$InsurancePolicyDtoToJson(_InsurancePolicyDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'plan': instance.plan,
      'user': instance.user,
      'booking': instance.booking,
      'policyNumber': instance.policyNumber,
      'status': instance.status,
      'startDate': instance.startDate,
      'endDate': instance.endDate,
      'totalPremium': instance.totalPremium,
      'certificateUrl': instance.certificateUrl,
      'createdAt': instance.createdAt,
    };
