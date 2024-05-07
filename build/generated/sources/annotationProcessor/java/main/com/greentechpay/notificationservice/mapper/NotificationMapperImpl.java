package com.greentechpay.notificationservice.mapper;

import com.greentechpay.notificationservice.dto.NotificationDto;
import com.greentechpay.notificationservice.entity.Notification;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-30T10:19:39+0400",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.5.jar, environment: Java 18.0.2.1 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDto entityToDto(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationDto notificationDto = new NotificationDto();

        notificationDto.setTitle( notification.getTitle() );
        notificationDto.setBody( notification.getBody() );
        if ( notification.getSendDate() != null ) {
            notificationDto.setSendDate( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( notification.getSendDate() ) );
        }

        return notificationDto;
    }
}
