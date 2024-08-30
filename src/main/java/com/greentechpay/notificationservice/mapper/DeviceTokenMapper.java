package com.greentechpay.notificationservice.mapper;

import com.greentechpay.notificationservice.kafka.dto.LoginDeviceTokenEvent;
import com.greentechpay.notificationservice.entity.UserDeviceToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceTokenMapper {

    //@Mapping(source = "userId", target = "user.id")
    UserDeviceToken dtoToEntity(LoginDeviceTokenEvent dto);
}
