import 'package:freezed_annotation/freezed_annotation.dart';

part 'analytics_dto.freezed.dart';
part 'analytics_dto.g.dart';

@freezed
abstract class AnalyticsDto with _$AnalyticsDto {
  const factory AnalyticsDto({
    @Default(0) int totalUsers,
    @Default(0) int totalTenants,
    @Default(0) int totalLandlords,
    @Default(0) int totalProperties,
    @Default(0) int pendingProperties,
    @Default(0) int approvedProperties,
    @Default(0) int totalBookings,
    @Default(0) int pendingBookings,
    @Default(0) int approvedBookings,
    @Default(0) int totalMessages,
    Map<String, int>? propertiesByType,
    Map<String, int>? propertiesByCity,
    Map<String, int>? bookingsByStatus,
  }) = _AnalyticsDto;

  factory AnalyticsDto.fromJson(Map<String, dynamic> json) => _$AnalyticsDtoFromJson(json);
}
