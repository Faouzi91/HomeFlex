// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'chat_room_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ChatRoomDto _$ChatRoomDtoFromJson(Map<String, dynamic> json) => _ChatRoomDto(
  id: json['id'] as String,
  propertyId: json['propertyId'] as String?,
  propertyTitle: json['propertyTitle'] as String?,
  tenant: UserDto.fromJson(json['tenant'] as Map<String, dynamic>),
  landlord: UserDto.fromJson(json['landlord'] as Map<String, dynamic>),
  lastMessageAt: json['lastMessageAt'] as String?,
  unreadCount: (json['unreadCount'] as num?)?.toInt(),
);

Map<String, dynamic> _$ChatRoomDtoToJson(_ChatRoomDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'propertyId': instance.propertyId,
      'propertyTitle': instance.propertyTitle,
      'tenant': instance.tenant,
      'landlord': instance.landlord,
      'lastMessageAt': instance.lastMessageAt,
      'unreadCount': instance.unreadCount,
    };
