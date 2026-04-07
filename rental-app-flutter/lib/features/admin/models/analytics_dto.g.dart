// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'analytics_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AnalyticsDto _$AnalyticsDtoFromJson(Map<String, dynamic> json) =>
    _AnalyticsDto(
      totalUsers: (json['totalUsers'] as num?)?.toInt() ?? 0,
      totalTenants: (json['totalTenants'] as num?)?.toInt() ?? 0,
      totalLandlords: (json['totalLandlords'] as num?)?.toInt() ?? 0,
      totalProperties: (json['totalProperties'] as num?)?.toInt() ?? 0,
      pendingProperties: (json['pendingProperties'] as num?)?.toInt() ?? 0,
      approvedProperties: (json['approvedProperties'] as num?)?.toInt() ?? 0,
      totalBookings: (json['totalBookings'] as num?)?.toInt() ?? 0,
      pendingBookings: (json['pendingBookings'] as num?)?.toInt() ?? 0,
      approvedBookings: (json['approvedBookings'] as num?)?.toInt() ?? 0,
      totalMessages: (json['totalMessages'] as num?)?.toInt() ?? 0,
      propertiesByType: (json['propertiesByType'] as Map<String, dynamic>?)
          ?.map((k, e) => MapEntry(k, (e as num).toInt())),
      propertiesByCity: (json['propertiesByCity'] as Map<String, dynamic>?)
          ?.map((k, e) => MapEntry(k, (e as num).toInt())),
      bookingsByStatus: (json['bookingsByStatus'] as Map<String, dynamic>?)
          ?.map((k, e) => MapEntry(k, (e as num).toInt())),
    );

Map<String, dynamic> _$AnalyticsDtoToJson(_AnalyticsDto instance) =>
    <String, dynamic>{
      'totalUsers': instance.totalUsers,
      'totalTenants': instance.totalTenants,
      'totalLandlords': instance.totalLandlords,
      'totalProperties': instance.totalProperties,
      'pendingProperties': instance.pendingProperties,
      'approvedProperties': instance.approvedProperties,
      'totalBookings': instance.totalBookings,
      'pendingBookings': instance.pendingBookings,
      'approvedBookings': instance.approvedBookings,
      'totalMessages': instance.totalMessages,
      'propertiesByType': instance.propertiesByType,
      'propertiesByCity': instance.propertiesByCity,
      'bookingsByStatus': instance.bookingsByStatus,
    };
