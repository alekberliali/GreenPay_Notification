package com.greentechpay.notificationservice.mapper;

import com.greentechpay.notificationservice.model.dto.response.NotificationDto;
import com.greentechpay.notificationservice.model.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationDto entityToDto(Notification notification);
}
