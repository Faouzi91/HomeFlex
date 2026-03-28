package com.realestate.rental.mapper;

import com.realestate.rental.dto.response.NotificationDto;
import com.realestate.rental.domain.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "type", expression = "java(notification.getNotificationType() != null ? notification.getNotificationType().name() : null)")
    NotificationDto toDto(Notification notification);
}
