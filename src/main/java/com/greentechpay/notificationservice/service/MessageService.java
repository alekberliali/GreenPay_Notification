package com.greentechpay.notificationservice.service;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.greentechpay.notificationservice.dto.NotificationMessageToAll;
import com.greentechpay.notificationservice.dto.PaymentNotificationMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final TokenService tokenService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    protected Message generateSimaMessage(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        Notification notification = Notification.builder()
                .setTitle(paymentNotificationMessageEvent.getTitle())
                .setBody(paymentNotificationMessageEvent.getBody().getDescription())
                .build();
        return Message.builder()
                .setToken(tokenService.getDeviceTokenByUserId(paymentNotificationMessageEvent.getUserId()))
                .setNotification(notification)
                .build();
    }
    protected Message generateSenderMessage(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        var body = paymentNotificationMessageEvent.getBody();
        Notification notification = Notification.builder()
                .setTitle(paymentNotificationMessageEvent.getTitle())
                .setBody("-" + body.getAmount() + " " + body.getCurrency() +
                        ", "+ body.getDate())
                .setImage(paymentNotificationMessageEvent.getImage())
                .build();
        return Message.builder()
                .setToken(tokenService.getDeviceTokenByUserId(paymentNotificationMessageEvent.getUserId()))
                .setNotification(notification)
                .build();
    }
    protected Message generateReceiverMessage(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        var requestBody = paymentNotificationMessageEvent.getReceiverBody();
        Notification receiverNotification = Notification.builder()
                .setTitle(paymentNotificationMessageEvent.getTitle())
                .setBody("+" + requestBody.getAmount() + " " + requestBody.getCurrency() +
                        ", "+ requestBody.getDate())
                .build();
        return Message.builder()
                .setToken(tokenService.getDeviceTokenByUserId(paymentNotificationMessageEvent.getReceiverUserId()))
                .setNotification(receiverNotification)
                .build();
    }
    protected MulticastMessage generateMultiMessage(NotificationMessageToAll notificationMessageToAll) {
        List<String> userIdList = new ArrayList<>(notificationMessageToAll.getUserIdList());
        var tokens = tokenService.getDeviceTokenListByUserIdList(userIdList);
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
