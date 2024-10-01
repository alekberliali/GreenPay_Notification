package com.greentechpay.notificationservice.service;

import com.google.firebase.messaging.*;
import com.greentechpay.notificationservice.exception.ForbiddenException;
import com.greentechpay.notificationservice.model.dto.NotificationMessageToAll;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.kafka.dto.SimaNotificationMessageEvent;
import com.greentechpay.notificationservice.model.enumarated.NotificationParty;
import com.greentechpay.notificationservice.model.enumarated.NotificationProcessType;
import com.greentechpay.notificationservice.model.enumarated.Status;
import com.greentechpay.notificationservice.model.enumarated.TransferType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static com.greentechpay.notificationservice.kafka.KafkaConfigs.*;

@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;
    private final NotificationService notificationService;
    private final MessageService messageService;
    private final TokenService tokenService;
    private final AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(FirebaseMessagingService.class);

    private Boolean checkUserId(PaymentNotificationMessageEvent event) {
        if (event.getSender().getUserId() != null) {
            Boolean isExist = tokenService.existsByUserId(event.getSender().getUserId());
            if (Boolean.TRUE.equals(isExist)) {
                return true;
            } else {
                logger.error("User with id: {} not found", event.getSender().getUserId());
                return false;
            }
        } else {
            return false;
        }
    }

    private Boolean checkReceiverUserId(PaymentNotificationMessageEvent event) {
        if (event.getReceiver().getUserId() != null) {
            Boolean isExist = tokenService.existsByUserId(event.getReceiver().getUserId());
            if (Boolean.TRUE.equals(isExist)) {
                return true;
            } else {
                logger.error("Receiver user with id: {} not found", event.getReceiver().getUserId());
                return false;
            }
        } else {
            return false;
        }
    }

    private void sendSimaNotification(Message message) {
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException exception) {
            logger.error("Sima notification: Failed to send notification: {}", exception.getMessage());
        }
    }

    @KafkaListener(topics = NOTIFICATION_SIMA_TOPIC, containerFactory = SIMA_NOTIFICATION_CONTAINER_FACTORY)
    public void sendSimaNotificationEvent(SimaNotificationMessageEvent event) {

        Boolean existsByUserId = tokenService.existsByUserId(event.getUserId());

        if (Boolean.FALSE.equals(existsByUserId)) {
            logger.error("Sima notification: This user id could not find: {}", event.getUserId());

        } else if (Boolean.FALSE == tokenService.isTokenValid(event.getUserId())) {
            logger.error("Sima notification: token is null");

        } else {
            sendSimaNotification(messageService.generateSimaMessage(event));
        }
    }

    @KafkaListener(topics = NOTIFICATION_PAYMENT_TOPIC, containerFactory = PAYMENT_NOTIFICATION_CONTAINER_FACTORY)
    public void sendPaymentNotificationByToken(PaymentNotificationMessageEvent event) {

        logger.info("title: {}, senderUserId: {}, receiverUserId: {}",
                event.getTitle(), event.getSender().getUserId(), event.getReceiver().getUserId());

        var existsByUserId = checkUserId(event);
        var existsByReceiverUserId = checkReceiverUserId(event);

        if (existsByUserId && existsByReceiverUserId) {
            balanceToBalanceNotification(event);
        } else if (existsByUserId || existsByReceiverUserId) {
            detectTransferType(event);
        } else {
            logger.error("Notification message not sent");
        }
    }

    private void detectTransferType(PaymentNotificationMessageEvent event) {
        if (event.getTransferType().equals(TransferType.BalanceToCard) ||
                event.getTransferType().equals(TransferType.BillingPayment)) {
            sendToSenderPaymentNotification(event);
        } else if (event.getTransferType().equals(TransferType.CardToBalance)) {
            sendToReceiverPaymentNotification(event);
        }
    }

    private void balanceToBalanceNotification(PaymentNotificationMessageEvent event) {

        if ((event.getTransferType() == TransferType.IbanToPhoneNumber) &&
                (event.getNotificationProcessType() == NotificationProcessType.CONTINUES)) {
            if ((event.getStatus() == Status.Pending)) {
                sendToPendingNotification(event);
            } else if (event.getStatus() == Status.Success) {
                sendToReceiverPaymentNotification(event);
            } else if (event.getStatus() == Status.Fail) {
                sendToSenderPaymentNotification(event);
            }
        } else {
            sendToSenderPaymentNotification(event);
            sendToReceiverPaymentNotification(event);
        }
    }

    private void sendToPendingNotification(PaymentNotificationMessageEvent event) {
        var senderMessage = messageService.generatePendingMessage(event);
        if ((event.getStatus() != null) &&
                (event.getStatus() == Status.Pending) &&
                (Boolean.TRUE == tokenService.isTokenValid(event.getSender().getUserId()))) {
            try {
                firebaseMessaging.send(senderMessage);
                logger.info("Pending notification sent to sender: {}", senderMessage);
                notificationService.create(event, NotificationParty.SENDER);
            } catch (FirebaseMessagingException exception) {
                logger.error("Failed to send sender pending notification: {}", exception.getMessage());
            }
        } else {
            logger.error("Pending notification unsupported status type");
        }
    }

    private void sendToSenderPaymentNotification(PaymentNotificationMessageEvent event) {
        var senderMessage = messageService.generateSenderMessage(event);
        if (event.getStatus() != null && tokenService.isTokenValid(event.getSender().getUserId()) &&
                ((event.getStatus() == Status.Success) ||
                        (event.getStatus() == Status.Fail) ||
                        (event.getStatus() == Status.TransactionSuccessfully))) {
            try {
                firebaseMessaging.send(senderMessage);
                logger.info("Notification sent to sender: {}", senderMessage);
                notificationService.create(event, NotificationParty.SENDER);
            } catch (FirebaseMessagingException exception) {
                logger.error("Failed to send sender notification: {}", exception.getMessage());
            }
        } else {
            logger.error("Sender notification unsupported status type");
        }
    }

    private void sendToReceiverPaymentNotification(PaymentNotificationMessageEvent event) {
        var receiverMessage = messageService.generateReceiverMessage(event);
        if (event.getStatus() != null && tokenService.isTokenValid(event.getReceiver().getUserId()) &&
                ((event.getStatus() == Status.Success) ||
                        (event.getStatus() == Status.Fail))) {
            try {
                firebaseMessaging.send(receiverMessage);
                logger.info("Notification sent to receiver: {}", receiverMessage);
                notificationService.create(event, NotificationParty.RECEIVER);
            } catch (FirebaseMessagingException exception) {
                logger.error("Failed to send receiver notification: {}", exception.getMessage());
            }
        } else {
            logger.error("Receiver notification unsupported status type");
        }
    }

    public int sendNotificationToManyUser(String agentName, String agentPassword, String agentId, String accessToken,
                                          String authorization, NotificationMessageToAll notificationMessageToAll)
            throws ExecutionException, InterruptedException {

        var hasPermission = authService.hasPermission(agentName, agentPassword, agentId, accessToken, authorization);

        if (Boolean.TRUE.equals(hasPermission)) {
            var message = messageService.generateMultiMessage(notificationMessageToAll);
            var result = firebaseMessaging.sendEachForMulticastAsync(message).get().getSuccessCount();
            notificationService.createAll(notificationMessageToAll);
            return result;
        } else {
            throw new ForbiddenException("You do not have authorization for this operation.");
        }
    }
}