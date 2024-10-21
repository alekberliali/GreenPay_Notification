package com.greentechpay.notificationservice.kafka.listener.process;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.kafka.listener.strategy.SenderNotificationStrategy;
import com.greentechpay.notificationservice.kafka.listener.strategy.ReceiverNotificationStrategy;
import com.greentechpay.notificationservice.kafka.listener.strategy.SendMessageStrategy;
import com.greentechpay.notificationservice.model.enumarated.NotificationParty;
import com.greentechpay.notificationservice.service.NotificationService;
import com.greentechpay.notificationservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("ibanToUid")
@RequiredArgsConstructor
public class IbanToUId implements SenderNotificationStrategy, ReceiverNotificationStrategy, SendMessageStrategy {
    private final TokenService tokenService;
    private final NotificationService notificationService;
    private final PaymentNotificationValidation validation;
    private final SendNotification sendNotification;
    private static final String IBAN_TO_IBAN_SENDER = "- %s %s, %s, %s";
    private static final String IBAN_TO_IBAN_RECEIVER = "+ %s %s, %s, %s";

    public Notification generateSenderNotification(PaymentNotificationMessageEvent event) {
        var requestBody = event.getSender();
        String message = String.format(IBAN_TO_IBAN_SENDER, requestBody.getAmount(), requestBody.getCurrency(),
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

    public Notification generateReceiverNotification(PaymentNotificationMessageEvent event) {
        var requestBody = event.getReceiver();
        String message = String.format(IBAN_TO_IBAN_RECEIVER, requestBody.getAmount(), requestBody.getCurrency(),
                requestBody.getDate(), event.getStatus());
        return Notification.builder()
                .setTitle(event.getTitle())
                .setBody(message)
                .build();
    }

    @Override
    public Message generateReceiverNotificationMessage(PaymentNotificationMessageEvent event) {
        return Message.builder()
                .setToken(tokenService.getDeviceTokenByUserId(event.getReceiver().getUserId()))
                .setNotification(generateReceiverNotification(event))
                .build();
    }

    @Override
    public void sendMessage(PaymentNotificationMessageEvent event) {
        notificationService.create(event, NotificationParty.SENDER);
        notificationService.create(event, NotificationParty.RECEIVER);
        Boolean isSenderValid = validation.senderValidation(event);
        Boolean isReceiverValid = validation.receiverValidation(event);
        if (isSenderValid && isReceiverValid) {
            var senderMessage = generateSenderNotificationMessage(event);
            var receiverMessage = generateReceiverNotificationMessage(event);
            sendNotification.sendSenderNotification(event, senderMessage);
            sendNotification.sendReceiverNotification(event, receiverMessage);
        }
    }
}
