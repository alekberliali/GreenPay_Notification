package com.greentechpay.notificationservice.dto;

import lombok.Data;

@Data
public class LoginDeviceTokenEvent {
   private String UserId;
   private String DeviceToken;

}
