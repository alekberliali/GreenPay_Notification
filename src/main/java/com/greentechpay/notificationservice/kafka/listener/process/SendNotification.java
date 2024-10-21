package com.greentechpay.notificationservice.kafka.listener.process;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendNotification {
    private final FirebaseMessaging firebaseMessaging;
    private final PaymentNotificationValidation validation;

    public void sendSenderNotification(PaymentNotificationMessageEvent event, Message message) {
        validation.senderValidation(event);
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send sender notification: {}", e.getMessage());
        }
    }

    public void sendReceiverNotification(PaymentNotificationMessageEvent event, Message message) {
        validation.receiverValidation(event);
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send receiver notification: {}", e.getMessage());
        }
    }
}
