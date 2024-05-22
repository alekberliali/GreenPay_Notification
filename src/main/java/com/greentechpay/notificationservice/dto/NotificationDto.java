package com.greentechpay.notificationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime sendDate;
    private Boolean readStatus;
}
