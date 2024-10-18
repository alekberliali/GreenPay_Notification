package com.greentechpay.notificationservice.listener.process;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.listener.strategy.ReceiverNotificationStrategy;
import com.greentechpay.notificationservice.listener.strategy.SendMessageStrategy;
import com.greentechpay.notificationservice.listener.strategy.SenderNotificationStrategy;
import com.greentechpay.notificationservice.model.dto.Body;
import com.greentechpay.notificationservice.model.enumarated.NotificationProcessType;
import com.greentechpay.notificationservice.model.enumarated.Status;
import com.greentechpay.notificationservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service("IbanToPhoneNumber")
@RequiredArgsConstructor
public class IbanToPhoneNumber implements SenderNotificationStrategy, ReceiverNotificationStrategy, SendMessageStrategy {

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

    public void continuesProcess(PaymentNotificationMessageEvent event) {
        Message senderMessage = generateSenderNotificationMessage(event);
        Message receiverMessage = generateReceiverNotificationMessage(event);

        if (event.getStatus() == Status.Pending) {
            sendNotification.sendSenderNotification(event, senderMessage);
        } else if (event.getNotificationProcessType() == NotificationProcessType.CONTINUES) {
            if (event.getStatus() == Status.Success) {
                sendNotification.sendReceiverNotification(event, receiverMessage);
            } else {
                sendNotification.sendSenderNotification(event, senderMessage);
            }
        }
    }

    @Override
    public void sendMessage(PaymentNotificationMessageEvent event) {
        validation.senderValidation(event);
        validation.receiverValidation(event);

        if (event.getNotificationProcessType() == NotificationProcessType.ONCE) {
            Message senderMessage = generateSenderNotificationMessage(event);
            Message receiverMessage = generateReceiverNotificationMessage(event);
            sendNotification.sendSenderNotification(event, senderMessage);
            sendNotification.sendReceiverNotification(event, receiverMessage);
        } else {
            continuesProcess(event);
        }
    }
}