package com.greentechpay.notificationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)

public class NotificationIsNotFound extends RuntimeException {
    public static final String MESSAGE = "Notification Not Found";
    private final Long id;

    public NotificationIsNotFound(Long id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return MESSAGE + id;
    }
}