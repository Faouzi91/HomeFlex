import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/vehicle_response.dart';
import '../models/vehicle_booking_response.dart';
import '../../../core/api/api_client.dart';
import 'package:dio/dio.dart';

final vehiclesProvider = FutureProvider<List<VehicleResponse>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/vehicles/search');
  final List<dynamic> content = response.data['content'];
  return content.map((json) => VehicleResponse.fromJson(json)).toList();
});

final vehicleSearchProvider =
    FutureProvider.family<List<VehicleResponse>, Map<String, dynamic>>((ref, params) async {
  final apiClient = ApiClient();
  final queryParams = Map<String, dynamic>.from(params)
    ..removeWhere((key, value) => value == null || value == '');
  final response = await apiClient.dio.get('/vehicles/search', queryParameters: queryParams);
  final List<dynamic> content = response.data['content'];
  return content.map((json) => VehicleResponse.fromJson(json)).toList();
});

final vehicleDetailProvider =
    FutureProvider.family<VehicleResponse, String>((ref, id) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/vehicles/$id');
  return VehicleResponse.fromJson(response.data);
});

final vehicleAvailabilityProvider =
    FutureProvider.family<bool, Map<String, String>>((ref, params) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get(
    '/vehicles/${params['id']}/availability',
    queryParameters: {
      'startDate': params['startDate'],
      'endDate': params['endDate'],
    },
  );
  return response.data == true;
});

final myVehicleBookingsProvider = FutureProvider<List<VehicleBookingResponse>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/vehicles/my-bookings');
  final List<dynamic> data = response.data['data'];
  return data.map((json) => VehicleBookingResponse.fromJson(json)).toList();
});

final vehicleBookingsProvider =
    FutureProvider.family<List<VehicleBookingResponse>, String>((ref, vehicleId) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/vehicles/$vehicleId/bookings');
  final List<dynamic> data = response.data['data'];
  return data.map((json) => VehicleBookingResponse.fromJson(json)).toList();
});

class VehicleNotifier {
  final _apiClient = ApiClient();

  Future<VehicleResponse> createVehicle(Map<String, dynamic> data) async {
    final response = await _apiClient.dio.post('/vehicles', data: data);
    return VehicleResponse.fromJson(response.data);
  }

  Future<VehicleResponse> updateVehicle(String id, Map<String, dynamic> data) async {
    final response = await _apiClient.dio.put('/vehicles/$id', data: data);
    return VehicleResponse.fromJson(response.data);
  }

  Future<void> deleteVehicle(String id) async {
    await _apiClient.dio.delete('/vehicles/$id');
  }

  Future<VehicleResponse> uploadImages(String id, List<String> imagePaths) async {
    final formData = FormData.fromMap({
      'images': await Future.wait(
        imagePaths.map((path) => MultipartFile.fromFile(path)),
      ),
    });
    final response = await _apiClient.dio.post(
      '/vehicles/$id/images',
      data: formData,
      options: Options(contentType: 'multipart/form-data'),
    );
    return VehicleResponse.fromJson(response.data);
  }

  Future<VehicleBookingResponse> createVehicleBooking({
    required String vehicleId,
    required String startDate,
    required String endDate,
    String? message,
  }) async {
    final response = await _apiClient.dio.post('/vehicles/$vehicleId/bookings', data: {
      'vehicleId': vehicleId,
      'startDate': startDate,
      'endDate': endDate,
      if (message != null) 'message': message,
    });
    return VehicleBookingResponse.fromJson(response.data);
  }

  Future<void> recordView(String id) async {
    try {
      await _apiClient.dio.post('/vehicles/$id/view');
    } catch (_) {}
  }
}

final vehicleNotifierProvider = Provider((ref) => VehicleNotifier());

// Paginated vehicle loading
class PaginatedVehicleState {
  final List<VehicleResponse> vehicles;
  final bool isLoading;
  final bool hasMore;
  final int currentPage;
  final Map<String, dynamic> filters;

  const PaginatedVehicleState({
    this.vehicles = const [],
    this.isLoading = false,
    this.hasMore = true,
    this.currentPage = 0,
    this.filters = const {},
  });

  PaginatedVehicleState copyWith({
    List<VehicleResponse>? vehicles,
    bool? isLoading,
    bool? hasMore,
    int? currentPage,
    Map<String, dynamic>? filters,
  }) {
    return PaginatedVehicleState(
      vehicles: vehicles ?? this.vehicles,
      isLoading: isLoading ?? this.isLoading,
      hasMore: hasMore ?? this.hasMore,
      currentPage: currentPage ?? this.currentPage,
      filters: filters ?? this.filters,
    );
  }
}

class PaginatedVehicleNotifier extends Notifier<PaginatedVehicleState> {
  @override
  PaginatedVehicleState build() {
    loadFirstPage();
    return const PaginatedVehicleState(isLoading: true);
  }

  final _apiClient = ApiClient();
  static const _pageSize = 20;

  Future<void> loadFirstPage({Map<String, dynamic>? filters}) async {
    state = PaginatedVehicleState(
      isLoading: true,
      filters: filters ?? state.filters,
    );

    try {
      final queryParams = <String, dynamic>{
        'page': 0,
        'size': _pageSize,
        ...state.filters,
      }..removeWhere((key, value) => value == null || value == '');

      final response = await _apiClient.dio.get('/vehicles/search', queryParameters: queryParams);
      final List<dynamic> content = response.data['content'];
      final totalPages = response.data['totalPages'] as int? ?? 1;
      final vehicles = content.map((json) => VehicleResponse.fromJson(json)).toList();

      state = state.copyWith(
        vehicles: vehicles,
        isLoading: false,
        hasMore: 0 < totalPages - 1,
        currentPage: 0,
      );
    } catch (e) {
      state = state.copyWith(isLoading: false);
    }
  }

  Future<void> loadNextPage() async {
    if (state.isLoading || !state.hasMore) return;

    state = state.copyWith(isLoading: true);
    final nextPage = state.currentPage + 1;

    try {
      final queryParams = <String, dynamic>{
        'page': nextPage,
        'size': _pageSize,
        ...state.filters,
      }..removeWhere((key, value) => value == null || value == '');

      final response = await _apiClient.dio.get('/vehicles/search', queryParameters: queryParams);
      final List<dynamic> content = response.data['content'];
      final totalPages = response.data['totalPages'] as int? ?? 1;
      final newVehicles = content.map((json) => VehicleResponse.fromJson(json)).toList();

      state = state.copyWith(
        vehicles: [...state.vehicles, ...newVehicles],
        isLoading: false,
        hasMore: nextPage < totalPages - 1,
        currentPage: nextPage,
      );
    } catch (e) {
      state = state.copyWith(isLoading: false);
    }
  }
}

final paginatedVehiclesProvider =
    NotifierProvider<PaginatedVehicleNotifier, PaginatedVehicleState>(
        PaginatedVehicleNotifier.new);
