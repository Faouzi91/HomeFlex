import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/review_dto.dart';
import '../../../core/api/api_client.dart';

final propertyReviewsProvider =
    FutureProvider.family<List<ReviewDto>, String>((ref, propertyId) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/reviews/property/$propertyId');
  final List<dynamic> data = response.data['data'];
  return data.map((json) => ReviewDto.fromJson(json)).toList();
});

final propertyAverageRatingProvider =
    FutureProvider.family<double, String>((ref, propertyId) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/reviews/property/$propertyId/average');
  return (response.data['data'] as num?)?.toDouble() ?? 0.0;
});

class ReviewNotifier {
  final _apiClient = ApiClient();

  Future<ReviewDto> submitReview({
    required String propertyId,
    required int rating,
    String? comment,
  }) async {
    final response = await _apiClient.dio.post('/reviews', data: {
      'propertyId': propertyId,
      'rating': rating,
      if (comment != null && comment.isNotEmpty) 'comment': comment,
    });
    return ReviewDto.fromJson(response.data);
  }

  Future<void> deleteReview(String id) async {
    await _apiClient.dio.delete('/reviews/$id');
  }
}

final reviewNotifierProvider = Provider((ref) => ReviewNotifier());
