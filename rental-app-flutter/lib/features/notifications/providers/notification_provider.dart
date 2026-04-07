import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/notification_dto.dart';
import '../../../core/api/api_client.dart';

final notificationsProvider =
    FutureProvider.family<List<NotificationDto>, bool>((ref, unreadOnly) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/notifications', queryParameters: {
    if (unreadOnly) 'unreadOnly': true,
  });
  final List<dynamic> data = response.data['data'];
  return data.map((json) => NotificationDto.fromJson(json)).toList();
});

final allNotificationsProvider = FutureProvider<List<NotificationDto>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/notifications');
  final List<dynamic> data = response.data['data'];
  return data.map((json) => NotificationDto.fromJson(json)).toList();
});

final unreadCountProvider = Provider<int>((ref) {
  final notifs = ref.watch(allNotificationsProvider);
  return notifs.whenOrNull(data: (list) => list.where((n) => !n.isRead).length) ?? 0;
});

class NotificationNotifier {
  final _apiClient = ApiClient();

  Future<void> markAsRead(String id) async {
    await _apiClient.dio.patch('/notifications/$id/read');
  }

  Future<void> markAllAsRead() async {
    await _apiClient.dio.patch('/notifications/read-all');
  }

  Future<void> deleteNotification(String id) async {
    await _apiClient.dio.delete('/notifications/$id');
  }
}

final notificationNotifierProvider = Provider((ref) => NotificationNotifier());
