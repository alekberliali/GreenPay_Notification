package com.greentechpay.notificationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
    public static final String MESSAGE = "You do not have authorization for this operation.";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
