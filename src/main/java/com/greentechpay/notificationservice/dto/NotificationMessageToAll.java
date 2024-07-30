package com.greentechpay.notificationservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class NotificationMessageToAll {
    private List<String> userIdList;
    private String title;
    private String body;
    private NotificationType notificationType;
    private String image;
}
