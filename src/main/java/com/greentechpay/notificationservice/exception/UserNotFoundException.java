package com.greentechpay.notificationservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class UserNotFoundException extends RuntimeException{
    private final String message;
}
