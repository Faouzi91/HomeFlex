package com.homeflex.core.mapper;

import com.homeflex.core.dto.response.NotificationDto;
import com.homeflex.core.domain.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "type", expression = "java(notification.getNotificationType() != null ? notification.getNotificationType().name() : null)")
    NotificationDto toDto(Notification notification);
}
