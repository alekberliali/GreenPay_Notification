package com.greentechpay.notificationservice.kafka.consumer.process;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.kafka.consumer.strategy.SenderNotificationStrategy;
import com.greentechpay.notificationservice.kafka.consumer.strategy.ReceiverNotificationStrategy;
import com.greentechpay.notificationservice.kafka.consumer.strategy.SendMessageStrategy;
import com.greentechpay.notificationservice.model.dto.Body;
import com.greentechpay.notificationservice.model.enumarated.NotificationParty;
import com.greentechpay.notificationservice.model.enumarated.NotificationProcessType;
import com.greentechpay.notificationservice.model.enumarated.Status;
import com.greentechpay.notificationservice.service.NotificationService;
import com.greentechpay.notificationservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("ibanToPhoneNumber")
@RequiredArgsConstructor
public class IbanToPhoneNumber implements SenderNotificationStrategy, ReceiverNotificationStrategy, SendMessageStrategy {

    private final NotificationService notificationService;
    private final TokenService tokenService;
    private final SendNotification sendNotification;
    private final PaymentNotificationValidation validation;
    private static final String IBAN_TO_PHONE_SENDER = "- %s %s, %s";
    private static final String IBAN_TO_PHONE_RECEIVER = "+ %s %s, %s, %s";

    public Notification generateSenderNotification(PaymentNotificationMessageEvent event) {
        var body = event.getSender();
        String message = String.format(IBAN_TO_PHONE_SENDER, body.getAmount(), body.getCurrency(), body.getDate());

        if (event.getStatus() != Status.Pending) {
            message = message + " " + body.getCurrency();
        }

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
        Body body = event.getReceiver();
        String message = String.format(IBAN_TO_PHONE_RECEIVER, body.getAmount(), body.getCurrency(), body.getDate(),
                event.getStatus());

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

    private void continuesProcess(PaymentNotificationMessageEvent event) {
        boolean isSenderValid = validation.senderValidation(event);
        boolean isReceiverValid = validation.receiverValidation(event);
        Message senderMessage = generateSenderNotificationMessage(event);
        Message receiverMessage = generateReceiverNotificationMessage(event);

        if (event.getStatus() == Status.Pending && isSenderValid) {
            sendNotification.sendSenderNotification(senderMessage);
        } else if (isReceiverValid) {
            if (event.getStatus() == Status.Success) {
                sendNotification.sendReceiverNotification(receiverMessage);
            } else if (isSenderValid){
                sendNotification.sendSenderNotification(senderMessage);
            }
        }
    }

    private void onceProcess(PaymentNotificationMessageEvent event) {
        boolean isSenderValid = validation.senderValidation(event);
        boolean isReceiverValid = validation.receiverValidation(event);

        if (isSenderValid) {
            Message senderMessage = generateSenderNotificationMessage(event);
            sendNotification.sendSenderNotification(senderMessage);
        }
        if (isReceiverValid) {
            Message receiverMessage = generateReceiverNotificationMessage(event);
            sendNotification.sendReceiverNotification(receiverMessage);
        }
    }

    @Override
    public void sendMessage(PaymentNotificationMessageEvent event) {
        notificationService.create(event, NotificationParty.SENDER);
        notificationService.create(event, NotificationParty.RECEIVER);
        if (event.getNotificationProcessType() == NotificationProcessType.ONCE) {
            onceProcess(event);
        } else {
            continuesProcess(event);
        }
    }
}