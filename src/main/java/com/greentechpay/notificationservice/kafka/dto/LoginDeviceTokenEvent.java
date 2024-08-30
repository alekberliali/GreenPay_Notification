package com.greentechpay.notificationservice.kafka.dto;

import lombok.Data;

@Data
public class LoginDeviceTokenEvent {
   private String UserId;
   private String DeviceToken;

}
