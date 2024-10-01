package com.greentechpay.notificationservice.kafka.dto;

import com.greentechpay.notificationservice.model.dto.Body;
import com.greentechpay.notificationservice.model.enumarated.NotificationProcessType;
import com.greentechpay.notificationservice.model.enumarated.Status;
import com.greentechpay.notificationservice.model.enumarated.TransferType;
import lombok.Data;

import java.util.Map;

@Data
public class PaymentNotificationMessageEvent {

    private String title;
    private Body sender;
    private Body receiver;
    private Status status;
    private String image;
    private NotificationProcessType notificationProcessType;
    private TransferType transferType;
    private Map<String, String> data;
}
