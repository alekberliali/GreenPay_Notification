package com.greentechpay.notificationservice.listener;

import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.listener.factory.NotificationFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.greentechpay.notificationservice.kafka.KafkaConfigs.NOTIFICATION_PAYMENT_TOPIC;
import static com.greentechpay.notificationservice.kafka.KafkaConfigs.PAYMENT_NOTIFICATION_CONTAINER_FACTORY;


@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationFactory notificationFactory;

    @KafkaListener(topics = NOTIFICATION_PAYMENT_TOPIC, containerFactory = PAYMENT_NOTIFICATION_CONTAINER_FACTORY)
    public void sendPaymentNotificationByToken(PaymentNotificationMessageEvent event) {
        notificationFactory.executeNotification(event);
    }
}
