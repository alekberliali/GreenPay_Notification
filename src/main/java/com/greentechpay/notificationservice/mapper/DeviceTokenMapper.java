package com.greentechpay.notificationservice.mapper;

import com.greentechpay.notificationservice.kafka.dto.LoginDeviceTokenEvent;
import com.greentechpay.notificationservice.model.entity.UserDeviceToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceTokenMapper {
    UserDeviceToken dtoToEntity(LoginDeviceTokenEvent dto);
}
