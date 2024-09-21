package com.greentechpay.notificationservice.kafka.dto;

import com.greentechpay.notificationservice.model.dto.Body;
import com.greentechpay.notificationservice.model.enumarated.TransferType;
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
    private TransferType transferType;
    private Map<String, String> data;
}
