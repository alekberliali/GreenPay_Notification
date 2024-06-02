package com.greentechpay.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.greentechpay.notificationservice.dto.NotificationMessageToAll;
import com.greentechpay.notificationservice.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.mapper.CustomNotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;
    private final TokenService tokenService;
    private final NotificationService notificationService;
    private final CustomNotificationMapper customNotificationMapper;

    @KafkaListener(topics = "Notification-Message", groupId = "1",
            containerFactory = "kafkaListenerContainerFactoryPaymentNotificationMessage")
    public String sendNotificationByToken(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        Notification notification = Notification.builder()
                .setTitle(paymentNotificationMessageEvent.getTitle())
                .setBody(paymentNotificationMessageEvent.getBody())
                .setImage(paymentNotificationMessageEvent.getImage())
                .build();
        Message message = Message.builder()
                .setToken(tokenService.getDeviceTokenByUserId(paymentNotificationMessageEvent.getUserId()))
                .setNotification(notification)
                .build();

        notificationService.create(customNotificationMapper
                .convertFromPaymentNotificationMessageEvent(paymentNotificationMessageEvent));
        try {
            firebaseMessaging.send(message);
            return "Success Sending Notification";
        } catch (FirebaseMessagingException exception) {
            exception.printStackTrace();
            return "Error Sending Notification";
        }
    }

    public String sendNotificationToManyUser(NotificationMessageToAll notificationMessageToAll) {
        Notification notification = Notification.builder()
                .setTitle(notificationMessageToAll.getTitle())
                .setBody(notificationMessageToAll.getBody())
                .setImage(notificationMessageToAll.getImage())
                .build();
        for (String userId : notificationMessageToAll.getUserIdList()) {
            Message message = Message.builder()
                    .setToken(tokenService.getDeviceTokenByUserId(userId))
                    .setNotification(notification)
                    .build();
            notificationService.createAll(customNotificationMapper.convertFromNotificationMessageAll(notificationMessageToAll));
            try {
                firebaseMessaging.send(message);
                return "Success Sending Notification";
            } catch (FirebaseMessagingException exception) {
                exception.printStackTrace();
                return "Error Sending Notification";
            }
        }
        return "";
    }
}
