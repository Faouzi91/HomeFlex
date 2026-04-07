import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/chat_room_dto.dart';
import '../models/message_dto.dart';
import '../../../core/api/api_client.dart';

final chatRoomsProvider = FutureProvider<List<ChatRoomDto>>((ref) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/chat/rooms');
  final List<dynamic> data = response.data;
  return data.map((json) => ChatRoomDto.fromJson(json)).toList();
});

final chatMessagesProvider =
    FutureProvider.family<List<MessageDto>, String>((ref, roomId) async {
  final apiClient = ApiClient();
  final response = await apiClient.dio.get('/chat/rooms/$roomId/messages');
  final List<dynamic> data = response.data;
  return data.map((json) => MessageDto.fromJson(json)).toList();
});

class ChatNotifier {
  final _apiClient = ApiClient();

  Future<ChatRoomDto> createRoom({
    required String propertyId,
    required String tenantId,
    required String landlordId,
  }) async {
    final response = await _apiClient.dio.post('/chat/rooms', data: {
      'propertyId': propertyId,
      'tenantId': tenantId,
      'landlordId': landlordId,
    });
    return ChatRoomDto.fromJson(response.data);
  }

  Future<MessageDto> sendMessage(String roomId, String message) async {
    final response = await _apiClient.dio.post('/chat/rooms/$roomId/messages', data: {
      'message': message,
    });
    return MessageDto.fromJson(response.data);
  }

  Future<void> markAsRead(String messageId) async {
    await _apiClient.dio.patch('/chat/messages/$messageId/read');
  }
}

final chatNotifierProvider = Provider((ref) => ChatNotifier());
