import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../properties/models/property_dto.dart';
import '../../auth/models/user_dto.dart';
import '../models/analytics_dto.dart';
import '../models/report_dto.dart';
import '../../../core/api/api_client.dart';

final pendingPropertiesProvider = FutureProvider<List<PropertyDto>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/admin/properties/pending');
  final List<dynamic> content = response.data['content'];
  return content.map((json) => PropertyDto.fromJson(json)).toList();
});

final allUsersProvider = FutureProvider<List<UserDto>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/admin/users');
  final List<dynamic> content = response.data['content'];
  return content.map((json) => UserDto.fromJson(json)).toList();
});

final analyticsProvider = FutureProvider<AnalyticsDto>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/admin/analytics');
  return AnalyticsDto.fromJson(response.data);
});

final reportsProvider = FutureProvider<List<ReportDto>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/admin/reports');
  final List<dynamic> content = response.data['content'];
  return content.map((json) => ReportDto.fromJson(json)).toList();
});

class AdminNotifier {
  final _apiClient = ApiClient();

  Future<PropertyDto> approveProperty(String id) async {
    final response = await _apiClient.dio.patch('/admin/properties/$id/approve');
    return PropertyDto.fromJson(response.data);
  }

  Future<PropertyDto> rejectProperty(String id, String reason) async {
    final response = await _apiClient.dio.patch('/admin/properties/$id/reject', data: {
      'reason': reason,
    });
    return PropertyDto.fromJson(response.data);
  }

  Future<UserDto> suspendUser(String id) async {
    final response = await _apiClient.dio.patch('/admin/users/$id/suspend');
    return UserDto.fromJson(response.data);
  }

  Future<UserDto> activateUser(String id) async {
    final response = await _apiClient.dio.patch('/admin/users/$id/activate');
    return UserDto.fromJson(response.data);
  }

  Future<ReportDto> resolveReport(String id, {String? reason}) async {
    final response = await _apiClient.dio.patch('/admin/reports/$id/resolve',
        data: reason != null ? {'reason': reason} : null);
    return ReportDto.fromJson(response.data);
  }
}

final adminNotifierProvider = Provider((ref) => AdminNotifier());
