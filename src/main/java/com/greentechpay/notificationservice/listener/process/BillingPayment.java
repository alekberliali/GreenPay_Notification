package com.greentechpay.notificationservice.listener.process;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.listener.strategy.SendMessageStrategy;
import com.greentechpay.notificationservice.listener.strategy.SenderNotificationStrategy;
import com.greentechpay.notificationservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("BillingPayment")
@RequiredArgsConstructor
public class BillingPayment implements SenderNotificationStrategy, SendMessageStrategy {

    private final TokenService tokenService;
    private final SendNotification sendNotification;
    private static final String BILLING_FORMAT = "- %s %s, %s, %s";

    public Notification generateSenderNotification(PaymentNotificationMessageEvent event) {
        var requestBody = event.getSender();
        String message = String.format(BILLING_FORMAT, requestBody.getAmount(), requestBody.getCurrency(),
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
        Message message = generateSenderNotificationMessage(event);
        sendNotification.sendSenderNotification(event, message);
    }
}
