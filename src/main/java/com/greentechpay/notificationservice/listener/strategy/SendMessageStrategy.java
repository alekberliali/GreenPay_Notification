package com.greentechpay.notificationservice.listener.strategy;

import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;

public interface SendMessageStrategy {
    void sendMessage(PaymentNotificationMessageEvent event);
}
