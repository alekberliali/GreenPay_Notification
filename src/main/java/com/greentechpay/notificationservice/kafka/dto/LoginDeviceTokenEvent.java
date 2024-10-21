package com.greentechpay.notificationservice.kafka.dto;

import lombok.Data;

@Data
public class LoginDeviceTokenEvent {
   private String userId;
   private String deviceToken;

}
