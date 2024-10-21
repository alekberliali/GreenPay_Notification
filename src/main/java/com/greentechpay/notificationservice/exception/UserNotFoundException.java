package com.greentechpay.notificationservice.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserNotFoundException extends RuntimeException {
    public static final String MESSAGE = "User not found";
    private final String id;

    public UserNotFoundException(String id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return MESSAGE + id;
    }
}
