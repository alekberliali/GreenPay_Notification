package com.greentechpay.notificationservice.kafka.listener.factory;

import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;

public interface NotificationFactory {
    void executeNotification(PaymentNotificationMessageEvent event);
}
