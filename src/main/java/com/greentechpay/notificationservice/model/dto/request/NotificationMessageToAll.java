package com.greentechpay.notificationservice.model.dto.request;

import com.greentechpay.notificationservice.model.enumarated.NotificationType;
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
