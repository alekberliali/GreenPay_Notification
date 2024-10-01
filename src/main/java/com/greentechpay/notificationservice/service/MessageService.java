package com.greentechpay.notificationservice.service;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.greentechpay.notificationservice.model.dto.NotificationMessageToAll;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.kafka.dto.SimaNotificationMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final TokenService tokenService;

    protected Message generateSimaMessage(SimaNotificationMessageEvent simaNotificationMessageEvent) {
        Notification notification = Notification.builder()
                .setTitle(simaNotificationMessageEvent.getTitle())
                .setBody(simaNotificationMessageEvent.getDescription())
                .build();
        return Message.builder()
                .setToken(tokenService.getDeviceTokenByUserId(simaNotificationMessageEvent.getUserId()))
                .setNotification(notification)
                .build();
    }

    protected Message generatePendingMessage(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        var body = paymentNotificationMessageEvent.getSender();
        Notification notification = Notification.builder()
                .setTitle(paymentNotificationMessageEvent.getTitle())
                .setBody("-" + body.getAmount() + " " + body.getCurrency() + ", " + body.getDate())
                .setImage(paymentNotificationMessageEvent.getImage())
                .build();
        return Message.builder()
                .setToken(tokenService.getDeviceTokenByUserId(paymentNotificationMessageEvent.getSender().getUserId()))
                .setNotification(notification)
                .build();
    }

    protected Message generateSenderMessage(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        var body = paymentNotificationMessageEvent.getSender();
        Notification notification = Notification.builder()
                .setTitle(paymentNotificationMessageEvent.getTitle())
                .setBody("-" + body.getAmount() + " " + body.getCurrency()
                        + ", " + body.getDate() + ", " + paymentNotificationMessageEvent.getStatus().name())
                .setImage(paymentNotificationMessageEvent.getImage())
                .build();
        return Message.builder()
                .setToken(tokenService.getDeviceTokenByUserId(paymentNotificationMessageEvent.getSender().getUserId()))
                .setNotification(notification)
                .build();
    }

    protected Message generateReceiverMessage(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        var requestBody = paymentNotificationMessageEvent.getReceiver();
        Notification receiverNotification = Notification.builder()
                .setTitle(paymentNotificationMessageEvent.getTitle())
                .setBody("+" + requestBody.getAmount() + " " + requestBody.getCurrency()
                        + ", " + requestBody.getDate() + ", " + paymentNotificationMessageEvent.getStatus())
                .build();
        return Message.builder()
                .setToken(tokenService.getDeviceTokenByUserId(paymentNotificationMessageEvent.getReceiver().getUserId()))
                .setNotification(receiverNotification)
                .build();
    }

    protected MulticastMessage generateMultiMessage(NotificationMessageToAll notificationMessageToAll) {
        List<String> userIdList = new ArrayList<>(notificationMessageToAll.getUserIdList());
        var results = tokenService.getDeviceTokenListByUserIdList(userIdList);
        var tokens = results.stream().filter(Objects::nonNull).toList();
        Notification notification = Notification.builder()
                .setTitle(notificationMessageToAll.getTitle())
                .setBody(notificationMessageToAll.getBody())
                .setImage(notificationMessageToAll.getImage())
                .build();
        return MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(notification)
                .build();
    }
}
