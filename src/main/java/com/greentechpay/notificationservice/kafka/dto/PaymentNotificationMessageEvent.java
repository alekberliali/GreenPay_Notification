package com.greentechpay.notificationservice.kafka.dto;

import com.greentechpay.notificationservice.dto.Body;
import lombok.Data;

import java.util.Map;

@Data
public class PaymentNotificationMessageEvent {

    private String Title;
    private String UserId;
    private com.greentechpay.notificationservice.dto.Body Body;
    private String ReceiverUserId;
    private Body ReceiverBody;
    private String Image;
    private Map<String, String> Data;
}
