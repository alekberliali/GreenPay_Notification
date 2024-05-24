package com.greentechpay.notificationservice.mapper;

import com.greentechpay.notificationservice.dto.LoginDeviceTokenEvent;
import com.greentechpay.notificationservice.entity.UserDeviceToken;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-23T15:51:47+0400",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 18.0.2.1 (Oracle Corporation)"
)
@Component
public class DeviceTokenMapperImpl implements DeviceTokenMapper {

    @Override
    public UserDeviceToken dtoToEntity(LoginDeviceTokenEvent dto) {
        if ( dto == null ) {
            return null;
        }

        UserDeviceToken userDeviceToken = new UserDeviceToken();

        userDeviceToken.setDeviceToken( dto.getDeviceToken() );
        userDeviceToken.setUserId( dto.getUserId() );

        return userDeviceToken;
    }
}
