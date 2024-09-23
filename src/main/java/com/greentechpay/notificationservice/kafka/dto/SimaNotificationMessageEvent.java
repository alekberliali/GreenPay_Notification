package com.greentechpay.notificationservice.kafka.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SimaNotificationMessageEvent {
    private String title;
    private String userId;
    private String description;
    private String image;
    private Map<String, String> data;
}
