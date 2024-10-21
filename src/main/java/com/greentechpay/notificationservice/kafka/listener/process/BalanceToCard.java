package com.greentechpay.notificationservice.kafka.listener.process;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.kafka.listener.strategy.SenderNotificationStrategy;
import com.greentechpay.notificationservice.kafka.listener.strategy.SendMessageStrategy;
import com.greentechpay.notificationservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("balanceToCard")
@RequiredArgsConstructor
public class BalanceToCard implements SenderNotificationStrategy, SendMessageStrategy {

    private final TokenService tokenService;
    private final PaymentNotificationValidation validation;
    private final SendNotification sendNotification;
    private static final String CARD_TO_BALANCE_FORMAT = "- %s %s, %s, %s";

    public Notification generateSenderNotification(PaymentNotificationMessageEvent event) {
        var requestBody = event.getSender();
        String message = String.format(CARD_TO_BALANCE_FORMAT, requestBody.getAmount(), requestBody.getCurrency(),
                requestBody.getDate(), event.getStatus());
        return Notification.builder()
                .setTitle(event.getTitle())
                .setBody(message)
                .build();
    }

    @Override
    public Message generateSenderNotificationMessage(PaymentNotificationMessageEvent event) {
        return Message.builder()
                .setToken(tokenService.getDeviceTokenByUserId(event.getSender().getUserId()))
                .setNotification(generateSenderNotification(event))
                .build();
    }

    @Override
    public void sendMessage(PaymentNotificationMessageEvent event) {
        Boolean isSenderValid = validation.senderValidation(event);
        if (Boolean.TRUE == isSenderValid) {
            Message message = generateSenderNotificationMessage(event);
            sendNotification.sendSenderNotification(event, message);
        }
    }
}
