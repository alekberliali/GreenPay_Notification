package com.greentechpay.notificationservice.mapper;

import com.greentechpay.notificationservice.dto.DeviceTokenDto;
import com.greentechpay.notificationservice.entity.UserDeviceToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeviceTokenMapper {

    //@Mapping(source = "userId", target = "user.id")
    UserDeviceToken dtoToEntity(DeviceTokenDto dto);
}
