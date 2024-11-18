package com.greentechpay.notificationservice.service;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.greentechpay.notificationservice.model.dto.NotificationMessageToAll;
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
