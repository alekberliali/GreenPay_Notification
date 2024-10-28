package com.greentechpay.notificationservice.kafka.listener.process;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.kafka.listener.strategy.ReceiverNotificationStrategy;
import com.greentechpay.notificationservice.kafka.listener.strategy.SendMessageStrategy;
import com.greentechpay.notificationservice.model.enumarated.NotificationParty;
import com.greentechpay.notificationservice.service.NotificationService;
import com.greentechpay.notificationservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("cardToBalance")
@RequiredArgsConstructor
public class CardToBalance implements ReceiverNotificationStrategy, SendMessageStrategy {

    private final TokenService tokenService;
    private final NotificationService notificationService;
    private final PaymentNotificationValidation validation;
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
        notificationService.create(event, NotificationParty.RECEIVER);
        boolean isReceiverValid = validation.receiverValidation(event);
        if (isReceiverValid) {
            Message message = generateReceiverNotificationMessage(event);
            sendNotification.sendReceiverNotification(message);
        }
    }
}
