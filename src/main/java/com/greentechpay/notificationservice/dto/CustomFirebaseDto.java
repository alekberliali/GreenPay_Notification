package com.greentechpay.notificationservice.dto;

import lombok.Data;

@Data
public class CustomFirebaseDto<T> {
    private T data;
    private String to;
}
