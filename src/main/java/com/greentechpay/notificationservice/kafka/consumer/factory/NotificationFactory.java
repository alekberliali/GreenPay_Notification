package com.greentechpay.notificationservice.kafka.consumer.factory;

import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;

public interface NotificationFactory {
    void executeNotification(PaymentNotificationMessageEvent event);
}
