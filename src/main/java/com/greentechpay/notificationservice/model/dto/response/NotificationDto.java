package com.greentechpay.notificationservice.model.dto.response;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class NotificationDto {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime sendDate;
    private Boolean readStatus;
}
