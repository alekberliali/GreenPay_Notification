package com.greentechpay.notificationservice.service;

import com.google.firebase.messaging.*;
import com.greentechpay.notificationservice.dto.NotificationMessageToAll;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutionException;

import static com.greentechpay.notificationservice.kafka.KafkaConfigs.*;
import static com.greentechpay.notificationservice.utils.ResponseMessage.*;

@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;
    private final NotificationService notificationService;
    private final MessageService messageService;

    @KafkaListener(topics = NOTIFICATION_TOPIC, containerFactory = NOTIFICATION_CONTAINER_FACTORY)
    public String sendNotificationByToken(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        if (paymentNotificationMessageEvent.getTitle().equals("SIMA")) {

            var message = messageService.generateSimaMessage(paymentNotificationMessageEvent);
            notificationService.create(paymentNotificationMessageEvent);

            try {
                firebaseMessaging.send(message);
                return SUCCESS;
            } catch (FirebaseMessagingException exception) {
                exception.printStackTrace();
                return ERROR;
            }
        } else if (paymentNotificationMessageEvent.getReceiverUserId() != null) {
            var message = messageService.generateSenderMessage(paymentNotificationMessageEvent);

            var receiverMessage = messageService.generateReceiverMessage(paymentNotificationMessageEvent);

            notificationService.create(paymentNotificationMessageEvent);

            try {
                firebaseMessaging.send(message);
                firebaseMessaging.send(receiverMessage);
                return SUCCESS;
            } catch (FirebaseMessagingException exception) {
                exception.printStackTrace();
                return ERROR;
            }

        } else {
            var message = messageService.generateSenderMessage(paymentNotificationMessageEvent);
            notificationService.create(paymentNotificationMessageEvent);

            try {
                firebaseMessaging.send(message);
                return SUCCESS;
            } catch (FirebaseMessagingException exception) {
                exception.printStackTrace();
                return ERROR;
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