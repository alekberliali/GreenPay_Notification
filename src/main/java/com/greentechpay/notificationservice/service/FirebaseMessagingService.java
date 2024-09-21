package com.greentechpay.notificationservice.service;

import com.google.firebase.messaging.*;
import com.greentechpay.notificationservice.model.dto.NotificationMessageToAll;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.kafka.dto.SimaNotificationMessageEvent;
import com.greentechpay.notificationservice.model.enumarated.TransferType;
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

    private Boolean checkUserId(PaymentNotificationMessageEvent event) {
        if (event.getUserId() != null) {
            Boolean isExist = tokenService.existsByUserId(event.getUserId());
            if (Boolean.TRUE.equals(isExist)) {
                return true;
            } else {
                logger.error("User with id: {} not found", event.getUserId());
                return false;
            }
        } else {
            return false;
        }
    }

    private Boolean checkReceiverUserId(PaymentNotificationMessageEvent event) {
        if (event.getReceiverUserId() != null) {
            Boolean isExist = tokenService.existsByUserId(event.getReceiverUserId());
            if (Boolean.TRUE.equals(isExist)) {
                return true;
            } else {
                logger.error("Receiver user with id: {} not found", event.getReceiverUserId());
                return false;
            }
        } else {
            return false;
        }
    }

    @KafkaListener(topics = NOTIFICATION_PAYMENT_TOPIC, containerFactory = PAYMENT_NOTIFICATION_CONTAINER_FACTORY)
    public void sendPaymentNotificationByToken(PaymentNotificationMessageEvent event) {

        logger.info("title: {}, senderUserId: {}, receiverUserId: {}",
                event.getTitle(), event.getUserId(), event.getReceiverUserId());

        var existsByUserId = checkUserId(event);
        var existsByReceiverUserId = checkReceiverUserId(event);

        if (existsByUserId && existsByReceiverUserId) {
            sendToSenderNotification(event);
            sendToReceiverNotification(event);
        } else if (existsByUserId && (event.getTransferType().equals(TransferType.BalanceToCard) ||
                event.getTransferType().equals(TransferType.BillingPayment))) {
            sendToSenderNotification(event);
        } else if (existsByReceiverUserId && (event.getTransferType().equals(TransferType.CardToBalance))) {
            sendToReceiverNotification(event);
        } else {
            logger.error("Notification message not sent");
        }
    }

    private String sendSimaNotification(Message message) {
        try {
            firebaseMessaging.send(message);
            return SUCCESS;
        } catch (FirebaseMessagingException exception) {
            logger.error("Sima notification: Failed to send notification: {}", exception.getMessage());
            return ERROR;
        }
    }

    @KafkaListener(topics = NOTIFICATION_SIMA_TOPIC, containerFactory = SIMA_NOTIFICATION_CONTAINER_FACTORY)
    public String sendSimaNotificationEvent(SimaNotificationMessageEvent event) {

        Boolean existsByUserId = tokenService.existsByUserId(event.getUserId());

        if (Boolean.FALSE.equals(existsByUserId)) {
            logger.error("Sima notification: This user id could not find: {}", event.getUserId());
            return null;
        } else {
            return sendSimaNotification(messageService.generateSimaMessage(event));
        }
    }


    private void sendToSenderNotification(PaymentNotificationMessageEvent event) {
        var senderMessage = messageService.generateSenderMessage(event);
        notificationService.create(event);
        try {
            firebaseMessaging.send(senderMessage);
        } catch (FirebaseMessagingException exception) {
            logger.error("Failed to send sender notification: {}", exception.getMessage());
        }
    }

    private void sendToReceiverNotification(PaymentNotificationMessageEvent event) {
        var receiverMessage = messageService.generateReceiverMessage(event);
        notificationService.create(event);
        try {
            firebaseMessaging.send(receiverMessage);
        } catch (FirebaseMessagingException exception) {
            logger.error("Failed to send receiver notification: {}", exception.getMessage());
        }
    }

    public int sendNotificationToManyUser(NotificationMessageToAll notificationMessageToAll)
            throws ExecutionException, InterruptedException {

        var message = messageService.generateMultiMessage(notificationMessageToAll);

        var result = firebaseMessaging.sendEachForMulticastAsync(message).get().getSuccessCount();
        notificationService.createAll(notificationMessageToAll);
        return result;
    }
}