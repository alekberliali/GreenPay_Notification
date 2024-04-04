package com.greentechpay.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class PaymentNotificationMessageEvent {
    private String UserId;
    private String Title;
    private String Body;
    private String ToUser;
    private String Image;
    private Map<String, String> Data;
}
