package com.greentechpay.notificationservice.service;

import com.google.firebase.messaging.*;
import com.greentechpay.notificationservice.exception.ForbiddenException;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.kafka.consumer.factory.NotificationFactory;
import com.greentechpay.notificationservice.model.dto.request.NotificationMessageToAll;
import com.greentechpay.notificationservice.kafka.dto.SimaNotificationMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static com.greentechpay.notificationservice.kafka.KafkaConfigs.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseMessagingService {
    private final FirebaseMessaging firebaseMessaging;
    private final NotificationService notificationService;
    private final MessageService messageService;
    private final TokenService tokenService;
    private final AuthService authService;
    private final NotificationFactory notificationFactory;

    private void sendSimaNotification(Message message) {
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException exception) {
            log.error("Sima notification: Failed to send notification: {}", exception.getMessage());
        }
    }

    @KafkaListener(topics = NOTIFICATION_SIMA_TOPIC, containerFactory = SIMA_NOTIFICATION_CONTAINER_FACTORY)
    @Retryable(maxAttempts = 1, backoff = @Backoff(delay = 0))
    public void sendSimaNotificationEvent(SimaNotificationMessageEvent event) {

        Boolean existsByUserId = tokenService.existsByUserId(event.getUserId());

        if (Boolean.FALSE.equals(existsByUserId)) {
            log.error("Sima notification: This user id could not find: {}", event.getUserId());
        } else if (Boolean.FALSE == tokenService.isTokenValid(event.getUserId())) {
            log.error("Sima notification: token is null");
        } else {
            sendSimaNotification(messageService.generateSimaMessage(event));
        }
    }

    @KafkaListener(topics = NOTIFICATION_PAYMENT_TOPIC, containerFactory = PAYMENT_NOTIFICATION_CONTAINER_FACTORY)
    @Retryable(maxAttempts = 1, backoff = @Backoff(delay = 0))
    public void sendPaymentNotificationByToken(PaymentNotificationMessageEvent event) {
        notificationFactory.executeNotification(event);
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
            throw new ForbiddenException();
        }
    }
}