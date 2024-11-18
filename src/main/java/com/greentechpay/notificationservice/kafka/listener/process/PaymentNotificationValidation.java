package com.greentechpay.notificationservice.kafka.listener.process;

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

    private boolean checkUserId(String userId) {
        var isExist = tokenService.existsByUserId(userId);
        if (Boolean.FALSE == isExist) {
            log.error("User id is not exist: {}", userId);
            return false;
        }
        return true;
    }

    protected Boolean senderValidation(PaymentNotificationMessageEvent event) {
        checkStatus(event.getStatus());
        boolean valid = checkUserId(event.getSender().getUserId());
        if (!valid) {return false;}
        Boolean isValid = tokenService.isTokenValid(event.getSender().getUserId());
        if (Boolean.FALSE == isValid) {
            log.error("Invalid token. Sender user id: {}", event.getSender().getUserId());
            return false;
        }
        return true;
    }

    protected Boolean receiverValidation(PaymentNotificationMessageEvent event) {
        checkStatus(event.getStatus());
        boolean valid = checkUserId(event.getReceiver().getUserId());
        if (!valid) {return false;}
        Boolean isValid = tokenService.isTokenValid(event.getReceiver().getUserId());
        if (Boolean.FALSE == isValid) {
            log.error("Invalid token. Receiver user id: {}", event.getReceiver().getUserId());
            return false;
        }
        return true;
    }
}
