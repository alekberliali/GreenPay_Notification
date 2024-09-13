package com.greentechpay.notificationservice.kafka.dto;

import com.greentechpay.notificationservice.dto.Body;
import lombok.Data;

import java.util.Map;

@Data
public class PaymentNotificationMessageEvent {

    private String title;
    private String userId;
    private Body body;
    private String receiverUserId;
    private Body receiverBody;
    private String image;
    private Map<String, String> data;
}
