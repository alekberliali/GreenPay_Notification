package com.greentechpay.notificationservice.service;

import com.google.firebase.messaging.*;
import com.greentechpay.notificationservice.dto.NotificationMessageToAll;
import com.greentechpay.notificationservice.dto.PaymentNotificationMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;
    private final NotificationService notificationService;
    private final MessageService messageService;

   // @KafkaListener(topics = "Notification-Message", containerFactory = "kafkaListenerContainerFactoryPaymentNotificationMessage")
    public String sendNotificationByToken(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        if (paymentNotificationMessageEvent.getTitle().equals("SIMA")) {

            var message = messageService.generateSimaMessage(paymentNotificationMessageEvent);
            notificationService.create(paymentNotificationMessageEvent);

            try {
                firebaseMessaging.send(message);
                return "Success Sending Notification";
            } catch (FirebaseMessagingException exception) {
                exception.printStackTrace();
                return "Error Sending Notification";
            }
        } else if (paymentNotificationMessageEvent.getReceiverUserId() != null) {
            var message = messageService.generateSenderMessage(paymentNotificationMessageEvent);

            var receiverMessage = messageService.generateReceiverMessage(paymentNotificationMessageEvent);

            notificationService.create(paymentNotificationMessageEvent);

            try {
                firebaseMessaging.send(message);
                firebaseMessaging.send(receiverMessage);
                return "Success Sending Notification";
            } catch (FirebaseMessagingException exception) {
                exception.printStackTrace();
                return "Error Sending Notification";
            }

        } else {
            var message = messageService.generateSenderMessage(paymentNotificationMessageEvent);
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

    public int sendNotificationToManyUser(NotificationMessageToAll notificationMessageToAll) throws ExecutionException, InterruptedException {

        var message = messageService.generateMultiMessage(notificationMessageToAll);

        var result = firebaseMessaging.sendEachForMulticastAsync(message).get().getSuccessCount();
        notificationService.createAll(notificationMessageToAll);
        return result;
    }
}
