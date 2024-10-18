package com.greentechpay.notificationservice.listener.factory;

import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;

public interface NotificationFactory {
    void executeNotification(PaymentNotificationMessageEvent event);
}
