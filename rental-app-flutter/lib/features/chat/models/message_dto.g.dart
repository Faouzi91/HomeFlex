// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'message_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_MessageDto _$MessageDtoFromJson(Map<String, dynamic> json) => _MessageDto(
  id: json['id'] as String,
  chatRoomId: json['chatRoomId'] as String,
  senderId: json['senderId'] as String,
  senderName: json['senderName'] as String,
  messageText: json['messageText'] as String,
  isRead: json['isRead'] as bool,
  createdAt: json['createdAt'] as String,
);

Map<String, dynamic> _$MessageDtoToJson(_MessageDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'chatRoomId': instance.chatRoomId,
      'senderId': instance.senderId,
      'senderName': instance.senderName,
      'messageText': instance.messageText,
      'isRead': instance.isRead,
      'createdAt': instance.createdAt,
    };
