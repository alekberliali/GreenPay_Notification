package com.greentechpay.notificationservice.mapper;

import com.greentechpay.notificationservice.dto.NotificationDto;
import com.greentechpay.notificationservice.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationDto entityToDto(Notification notification);
}
