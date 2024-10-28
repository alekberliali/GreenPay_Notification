package com.greentechpay.notificationservice.kafka.listener.process;

import com.greentechpay.notificationservice.exception.UserNotFoundException;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.model.enumarated.Status;
import com.greentechpay.notificationservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentNotificationValidation {
    private final TokenService tokenService;

    private static void checkStatus(Status status) {
        var isExist = Status.getStatusList().contains(status);
        if (!isExist) {
            throw new IllegalArgumentException("Invalid status " + status);
        }
    }

    private void checkUserId(String userId) {
        var isExist = tokenService.existsByUserId(userId);
        if (Boolean.FALSE == isExist) {
            throw new UserNotFoundException(userId);
        }
    }

    protected Boolean senderValidation(PaymentNotificationMessageEvent event) {
        checkStatus(event.getStatus());
        checkUserId(event.getSender().getUserId());
        Boolean isValid = tokenService.isTokenValid(event.getSender().getUserId());
        if (Boolean.FALSE == isValid) {
            log.error("Invalid token sender user id: {}", event.getSender().getUserId());
            return false;
        }
        return true;
    }

    protected Boolean receiverValidation(PaymentNotificationMessageEvent event) {
        checkStatus(event.getStatus());
        checkUserId(event.getReceiver().getUserId());
        Boolean isValid = tokenService.isTokenValid(event.getReceiver().getUserId());
        if (Boolean.FALSE == isValid) {
            log.error("Invalid token receiver user id: {}", event.getReceiver().getUserId());
            return false;
        }
        return true;
    }
}
