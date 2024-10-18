package com.greentechpay.notificationservice.listener.process;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.listener.strategy.ReceiverNotificationStrategy;
import com.greentechpay.notificationservice.listener.strategy.SendMessageStrategy;
import com.greentechpay.notificationservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("CardToBalance")
@RequiredArgsConstructor
public class CardToBalance implements ReceiverNotificationStrategy, SendMessageStrategy {

    private final TokenService tokenService;
    private final SendNotification sendNotification;
    private static final String CARD_TO_BALANCE = "+ %s %s, %s, %s";

    @Override
    public Message generateReceiverNotificationMessage(PaymentNotificationMessageEvent event) {
        var requestBody = event.getReceiver();
        String message = String.format(CARD_TO_BALANCE, requestBody.getAmount(), requestBody.getCurrency(),
                requestBody.getDate(), event.getStatus());

        Notification notification = Notification.builder()
                .setTitle(event.getTitle())
                .setBody(message)
                .build();

        return Message.builder()
                .setToken(tokenService.getDeviceTokenByUserId(event.getReceiver().getUserId()))
                .setNotification(notification)
                .build();
    }

    @Override
    public void sendMessage(PaymentNotificationMessageEvent event) {
        Message message = generateReceiverNotificationMessage(event);
        sendNotification.sendReceiverNotification(event, message);
    }
}
