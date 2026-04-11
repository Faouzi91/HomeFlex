import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:dio/dio.dart';
import '../models/maintenance_request_dto.dart';
import '../../../core/api/api_client.dart';

final myMaintenanceRequestsProvider =
    FutureProvider<List<MaintenanceRequestDto>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/maintenance/my');
  final List<dynamic> data = response.data;
  return data.map((json) => MaintenanceRequestDto.fromJson(json)).toList();
});

final landlordMaintenanceRequestsProvider =
    FutureProvider<List<MaintenanceRequestDto>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/maintenance/landlord');
  final List<dynamic> data = response.data;
  return data.map((json) => MaintenanceRequestDto.fromJson(json)).toList();
});

final maintenanceRequestDetailProvider =
    FutureProvider.family<MaintenanceRequestDto, String>((ref, id) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/maintenance/$id');
  return MaintenanceRequestDto.fromJson(response.data);
});

class MaintenanceNotifier extends Notifier<void> {
  @override
  void build() {}

  final _apiClient = ApiClient();

  Future<MaintenanceRequestDto> createRequest(
      MaintenanceRequestCreateRequest request) async {
    final response = await _apiClient.dio.post(
      '/maintenance',
      data: request.toJson(),
    );
    return MaintenanceRequestDto.fromJson(response.data);
  }

  Future<void> uploadImages(String requestId, List<MultipartFile> files) async {
    final formData = FormData.fromMap({
      'files': files,
    });
    await _apiClient.dio.post(
      '/maintenance/$requestId/images',
      data: formData,
      options: Options(contentType: 'multipart/form-data'),
    );
  }

  Future<MaintenanceRequestDto> updateStatus(
      String requestId, MaintenanceStatusUpdateRequest update) async {
    final response = await _apiClient.dio.patch(
      '/maintenance/$requestId/status',
      data: update.toJson(),
    );
    return MaintenanceRequestDto.fromJson(response.data);
  }
}

final maintenanceNotifierProvider =
    NotifierProvider<MaintenanceNotifier, void>(MaintenanceNotifier.new);
