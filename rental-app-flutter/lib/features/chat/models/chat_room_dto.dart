import 'package:freezed_annotation/freezed_annotation.dart';
import '../../auth/models/user_dto.dart';

part 'chat_room_dto.freezed.dart';
part 'chat_room_dto.g.dart';

@freezed
abstract class ChatRoomDto with _$ChatRoomDto {
  const factory ChatRoomDto({
    required String id,
    String? propertyId,
    String? propertyTitle,
    required UserDto tenant,
    required UserDto landlord,
    String? lastMessageAt,
    int? unreadCount,
  }) = _ChatRoomDto;

  factory ChatRoomDto.fromJson(Map<String, dynamic> json) => _$ChatRoomDtoFromJson(json);
}
