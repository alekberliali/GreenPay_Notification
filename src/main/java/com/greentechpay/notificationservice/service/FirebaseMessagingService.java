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

    @KafkaListener(topics = "Notification-Message", groupId = "1",
            containerFactory = "kafkaListenerContainerFactoryPaymentNotificationMessage")
    public String sendNotificationByToken(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        System.out.println(paymentNotificationMessageEvent.getBody());
        Notification notification = Notification.builder()
                .setTitle(paymentNotificationMessageEvent.getTitle())
                .setBody(paymentNotificationMessageEvent.getBody())
                .setImage(paymentNotificationMessageEvent.getImage())
                .build();

        Message message = Message.builder()
                .setToken(tokenService.getDeviceTokenByUserId(paymentNotificationMessageEvent.getUserId()))
                .setNotification(notification)
                // .putAllData(paymentNotificationMessageEvent.getData())
                .build();
        notificationService.create(paymentNotificationMessageEvent);

        try {
            firebaseMessaging.send(message);
            return "Success Sending Notification";
        } catch (FirebaseMessagingException exception) {
            exception.printStackTrace();
            return "Error Sending Notification";
        }
    }
}
