import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/property_dto.dart';
import '../../../core/api/api_client.dart';
import 'package:dio/dio.dart';

final propertiesProvider = FutureProvider<List<PropertyDto>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/properties/search');
  final List<dynamic> content = response.data['content'];
  return content.map((json) => PropertyDto.fromJson(json)).toList();
});

final propertySearchProvider =
    FutureProvider.family<List<PropertyDto>, Map<String, dynamic>>((ref, params) async {
  final apiClient = ApiClient();
  final queryParams = Map<String, dynamic>.from(params)
    ..removeWhere((key, value) => value == null || value == '');
  final response = await apiClient.dio.get('/properties/search', queryParameters: queryParams);
  final List<dynamic> content = response.data['content'];
  return content.map((json) => PropertyDto.fromJson(json)).toList();
});

final propertyDetailProvider =
    FutureProvider.family<PropertyDto, String>((ref, id) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/properties/$id');
  return PropertyDto.fromJson(response.data);
});

final myPropertiesProvider = FutureProvider<List<PropertyDto>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/properties/my-properties');
  final List<dynamic> data = response.data['data'];
  return data.map((json) => PropertyDto.fromJson(json)).toList();
});

final similarPropertiesProvider =
    FutureProvider.family<List<PropertyDto>, String>((ref, id) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/properties/$id/similar');
  final List<dynamic> data = response.data['data'];
  return data.map((json) => PropertyDto.fromJson(json)).toList();
});

class PropertyNotifier {
  final _apiClient = ApiClient();

  Future<void> recordView(String id) async {
    try {
      await _apiClient.dio.post('/properties/$id/view');
    } catch (_) {}
  }

  Future<PropertyDto> createProperty(FormData formData) async {
    final response = await _apiClient.dio.post(
      '/properties',
      data: formData,
      options: Options(contentType: 'multipart/form-data'),
    );
    return PropertyDto.fromJson(response.data);
  }

  Future<PropertyDto> updateProperty(String id, FormData formData) async {
    final response = await _apiClient.dio.put(
      '/properties/$id',
      data: formData,
      options: Options(contentType: 'multipart/form-data'),
    );
    return PropertyDto.fromJson(response.data);
  }

  Future<void> deleteProperty(String id) async {
    await _apiClient.dio.delete('/properties/$id');
  }

  Future<void> reportProperty(String id, {required String reason, String? description}) async {
    await _apiClient.dio.post('/properties/$id/report', data: {
      'reason': reason,
      'description': ?description,
    });
  }
}

final propertyNotifierProvider = Provider((ref) => PropertyNotifier());

// Paginated property loading
class PaginatedPropertyState {
  final List<PropertyDto> properties;
  final bool isLoading;
  final bool hasMore;
  final int currentPage;
  final Map<String, dynamic> filters;
  final String? error;

  const PaginatedPropertyState({
    this.properties = const [],
    this.isLoading = false,
    this.hasMore = true,
    this.currentPage = 0,
    this.filters = const {},
    this.error,
  });

  PaginatedPropertyState copyWith({
    List<PropertyDto>? properties,
    bool? isLoading,
    bool? hasMore,
    int? currentPage,
    Map<String, dynamic>? filters,
    String? error,
  }) {
    return PaginatedPropertyState(
      properties: properties ?? this.properties,
      isLoading: isLoading ?? this.isLoading,
      hasMore: hasMore ?? this.hasMore,
      currentPage: currentPage ?? this.currentPage,
      filters: filters ?? this.filters,
      error: error,
    );
  }
}

class PaginatedPropertyNotifier extends Notifier<PaginatedPropertyState> {
  @override
  PaginatedPropertyState build() {
    Future.microtask(loadFirstPage);
    return const PaginatedPropertyState(isLoading: true);
  }

  final _apiClient = ApiClient();
  static const _pageSize = 20;

  Future<void> loadFirstPage({Map<String, dynamic>? filters}) async {
    state = PaginatedPropertyState(
      isLoading: true,
      filters: filters ?? state.filters,
    );

    try {
      final queryParams = <String, dynamic>{
        'page': 0,
        'size': _pageSize,
        ...state.filters,
      }..removeWhere((key, value) => value == null || value == '');

      final response = await _apiClient.dio.get('/properties/search', queryParameters: queryParams);
      final List<dynamic> content = response.data['content'];
      final totalPages = response.data['totalPages'] as int? ?? 1;
      final properties = content.map((json) => PropertyDto.fromJson(json)).toList();

      state = state.copyWith(
        properties: properties,
        isLoading: false,
        hasMore: 0 < totalPages - 1,
        currentPage: 0,
      );
    } catch (e) {
      state = state.copyWith(isLoading: false, error: 'Failed to load properties');
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

      final response = await _apiClient.dio.get('/properties/search', queryParameters: queryParams);
      final List<dynamic> content = response.data['content'];
      final totalPages = response.data['totalPages'] as int? ?? 1;
      final newProperties = content.map((json) => PropertyDto.fromJson(json)).toList();

      state = state.copyWith(
        properties: [...state.properties, ...newProperties],
        isLoading: false,
        hasMore: nextPage < totalPages - 1,
        currentPage: nextPage,
      );
    } catch (e) {
      state = state.copyWith(isLoading: false, error: 'Failed to load properties');
    }
  }
}

final paginatedPropertiesProvider =
    NotifierProvider<PaginatedPropertyNotifier, PaginatedPropertyState>(
        PaginatedPropertyNotifier.new);
