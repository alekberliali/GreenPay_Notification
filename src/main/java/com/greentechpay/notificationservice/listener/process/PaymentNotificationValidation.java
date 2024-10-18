package com.greentechpay.notificationservice.listener.process;

import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.model.enumarated.Status;
import com.greentechpay.notificationservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentNotificationValidation {
    private final TokenService tokenService;
    private static final Logger logger = LoggerFactory.getLogger(PaymentNotificationValidation.class);

    private static void checkStatus(Status status) {
        var isExist = Status.getStatusList().contains(status);
        if (!isExist) {
            throw new IllegalArgumentException("Invalid status " + status);
        }
    }

    protected void senderValidation(PaymentNotificationMessageEvent event) {
        checkStatus(event.getStatus());
        Boolean isValid = tokenService.isTokenValid(event.getSender().getUserId());
        if (Boolean.FALSE == isValid) {
            logger.error("sender user token is not exist: {}", event.getSender().getUserId());
            throw new IllegalArgumentException("Invalid token " + event.getSender().getUserId());
        }
    }

    protected void receiverValidation(PaymentNotificationMessageEvent event) {
        checkStatus(event.getStatus());
        Boolean isValid = tokenService.isTokenValid(event.getReceiver().getUserId());
        if (Boolean.FALSE == isValid) {
            logger.error("receiver user token is not exist: {}", event.getReceiver().getUserId());
            throw new IllegalArgumentException("Invalid token " + event.getReceiver().getUserId());
        }
    }
}
