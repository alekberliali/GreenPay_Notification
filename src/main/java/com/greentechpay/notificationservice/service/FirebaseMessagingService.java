package com.greentechpay.notificationservice.service;

import com.google.firebase.messaging.*;
import com.greentechpay.notificationservice.dto.NotificationMessageToAll;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final TokenService tokenService;

    private static final Logger logger = LoggerFactory.getLogger(FirebaseMessagingService.class);

    @KafkaListener(topics = NOTIFICATION_TOPIC, containerFactory = NOTIFICATION_CONTAINER_FACTORY)
    public String sendNotificationByToken(PaymentNotificationMessageEvent event) {

        logger.info("title: {}, senderUserId: {}, receiverUserId: {}",
                event.getTitle(), event.getUserId(), event.getReceiverUserId());

        Boolean existsByUserId = tokenService.existsByUserId(event.getUserId());
        Boolean existsByReceiverUserId = tokenService.existsByUserId(event.getReceiverUserId());

        if (Boolean.FALSE.equals(existsByUserId)) {
            logger.error("This user id could not find: {}", event.getUserId());
            return null;
        }

        if (Boolean.FALSE.equals(existsByReceiverUserId)) {
            return handleSingleUserNotification(event);
        }

        return handleBothUserNotification(event);
    }

    private String handleBothUserNotification(PaymentNotificationMessageEvent event) {
        var senderMessage = messageService.generateSenderMessage(event);
        var receiverMessage = messageService.generateReceiverMessage(event);
        notificationService.create(event);

        try {
            firebaseMessaging.send(senderMessage);
            firebaseMessaging.send(receiverMessage);
            return SUCCESS;
        } catch (FirebaseMessagingException exception) {
            logger.error("Failed to send sender and receiver notification: {}", exception.getMessage());
            return ERROR;
        }
    }

    private String handleSingleUserNotification(PaymentNotificationMessageEvent event) {
        if ("SIMA".equals(event.getTitle())) {
            return sendNotification(event, messageService.generateSimaMessage(event));
        } else {
            return sendNotification(event, messageService.generateSenderMessage(event));
        }
    }

    private String sendNotification(PaymentNotificationMessageEvent event, Message message) {
        notificationService.create(event);
        try {
            firebaseMessaging.send(message);
            return SUCCESS;
        } catch (FirebaseMessagingException exception) {
            logger.error("Failed to send notification: {}", exception.getMessage());
            return ERROR;
        }
    }

    public int sendNotificationToManyUser(NotificationMessageToAll notificationMessageToAll) throws ExecutionException, InterruptedException {

        var message = messageService.generateMultiMessage(notificationMessageToAll);

        var result = firebaseMessaging.sendEachForMulticastAsync(message).get().getSuccessCount();
        notificationService.createAll(notificationMessageToAll);
        return result;
    }
}