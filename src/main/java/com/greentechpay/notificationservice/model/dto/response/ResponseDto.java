package com.greentechpay.notificationservice.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDto<T> {
    private T data;
}
