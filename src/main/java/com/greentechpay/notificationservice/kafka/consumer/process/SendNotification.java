package com.greentechpay.notificationservice.kafka.consumer.process;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendNotification {
    private final FirebaseMessaging firebaseMessaging;

    public void sendSenderNotification(Message message) {
        try {
            firebaseMessaging.send(message);
            log.info("Message sent to sender successfully");
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send sender notification: {}", e.getMessage());
        }
    }

    public void sendReceiverNotification(Message message) {
        try {
            firebaseMessaging.send(message);
            log.info("Message sent to receiver successfully");
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send receiver notification: {}", e.getMessage());
        }
    }
}
