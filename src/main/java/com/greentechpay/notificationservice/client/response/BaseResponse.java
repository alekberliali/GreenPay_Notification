package com.greentechpay.notificationservice.client.response;

import lombok.Data;

import java.util.List;

@Data
public class BaseResponse<T> {
    private T data;
    private List<String> errors;
}
