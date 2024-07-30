package com.greentechpay.notificationservice.mapper;

import com.greentechpay.notificationservice.dto.NotificationDto;
import com.greentechpay.notificationservice.entity.Notification;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-10T14:19:53+0400",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDto entityToDto(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationDto notificationDto = new NotificationDto();

        notificationDto.setId( notification.getId() );
        notificationDto.setTitle( notification.getTitle() );
        notificationDto.setBody( notification.getBody() );
        notificationDto.setSendDate( notification.getSendDate() );
        notificationDto.setReadStatus( notification.getReadStatus() );

        return notificationDto;
    }
}
