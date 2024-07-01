package com.greentechpay.notificationservice.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PaymentNotificationMessageEvent {

    private String Title;
    private String UserId;
    private Body Body;
    private String ReceiverUserId;
    private Body ReceiverBody;
    private String Image;
    private Map<String, String> Data;
}
