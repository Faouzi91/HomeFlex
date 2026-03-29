package com.homeflex.core.mapper;

import com.homeflex.core.dto.response.ChatRoomDto;
import com.homeflex.core.dto.response.MessageDto;
import com.homeflex.core.domain.entity.ChatRoom;
import com.homeflex.core.domain.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ChatMapper {

    @Mapping(target = "propertyId", source = "chatRoom.property.id")
    @Mapping(target = "propertyTitle", source = "chatRoom.property.title")
    @Mapping(target = "unreadCount", source = "unreadCount")
    ChatRoomDto toDto(ChatRoom chatRoom, Integer unreadCount);

    @Mapping(target = "chatRoomId", source = "chatRoom.id")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderName", expression = "java(message.getSender().getFirstName() + \" \" + message.getSender().getLastName())")
    MessageDto toDto(Message message);
}
