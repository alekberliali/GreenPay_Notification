package com.greentechpay.notificationservice.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.greentechpay.notificationservice.dto.PaymentNotificationMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;
    private final TokenService tokenService;
    private final NotificationService notificationService;

    @KafkaListener(topics = "Notification-Message", groupId = "1")
    public String sendNotificationByToken(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        Notification notification = Notification.builder()
                .setTitle(paymentNotificationMessageEvent.getTitle())
                .setBody(paymentNotificationMessageEvent.getBody())
                .setImage(paymentNotificationMessageEvent.getImage())
                .build();
        System.out.println(paymentNotificationMessageEvent.getUserId());
        System.out.println(paymentNotificationMessageEvent.getTitle());
        System.out.println(paymentNotificationMessageEvent.getBody());
        System.out.println(paymentNotificationMessageEvent.getToUser());
        if (tokenService.getUserDeviceTokenByUserId(paymentNotificationMessageEvent.getUserId()).isActive()) {
            Message message = Message.builder()
                    .setToken(tokenService.getDeviceTokenByUserId(paymentNotificationMessageEvent.getUserId()))
                    .setNotification(notification)
                    .putAllData(paymentNotificationMessageEvent.getData())
                    .build();
            notificationService.create(paymentNotificationMessageEvent);

            try {
                firebaseMessaging.send(message);
                return "Success Sending Notification";
            } catch (FirebaseMessagingException exception) {
                exception.printStackTrace();
                return "Error Sending Notification";
            }
        } else {
            return "This token is passive";
        }
    }
}
