import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../properties/models/property_dto.dart';
import '../../../core/api/api_client.dart';
import 'package:dio/dio.dart';

class FavoriteState {
  final Set<String> favoriteIds;
  final bool isLoading;

  FavoriteState({this.favoriteIds = const {}, this.isLoading = false});

  FavoriteState copyWith({Set<String>? favoriteIds, bool? isLoading}) {
    return FavoriteState(
      favoriteIds: favoriteIds ?? this.favoriteIds,
      isLoading: isLoading ?? this.isLoading,
    );
  }
}

class FavoriteNotifier extends Notifier<FavoriteState> {
  @override
  FavoriteState build() => FavoriteState();

  final _apiClient = ApiClient();

  Future<void> toggleFavorite(String propertyId) async {
    final isFavorited = state.favoriteIds.contains(propertyId);
    final newIds = Set<String>.from(state.favoriteIds);

    if (isFavorited) {
      newIds.remove(propertyId);
      state = state.copyWith(favoriteIds: newIds);
      try {
        await _apiClient.dio.delete('/favorites/$propertyId');
      } on DioException {
        newIds.add(propertyId);
        state = state.copyWith(favoriteIds: newIds);
      }
    } else {
      newIds.add(propertyId);
      state = state.copyWith(favoriteIds: newIds);
      try {
        await _apiClient.dio.post('/favorites/$propertyId');
      } on DioException {
        newIds.remove(propertyId);
        state = state.copyWith(favoriteIds: newIds);
      }
    }
  }

  Future<void> checkFavorite(String propertyId) async {
    try {
      final response = await _apiClient.dio.get('/favorites/check/$propertyId');
      final isFavorite = response.data['data'] == true;
      final newIds = Set<String>.from(state.favoriteIds);
      if (isFavorite) {
        newIds.add(propertyId);
      } else {
        newIds.remove(propertyId);
      }
      state = state.copyWith(favoriteIds: newIds);
    } on DioException {
      // ignore
    }
  }
}

final favoriteProvider = NotifierProvider<FavoriteNotifier, FavoriteState>(FavoriteNotifier.new);

final favoritesListProvider = FutureProvider<List<PropertyDto>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/favorites');
  final List<dynamic> data = response.data['data'];
  return data.map((json) => PropertyDto.fromJson(json)).toList();
});
