package com.greentechpay.notificationservice.kafka.listener.strategy;

import com.google.firebase.messaging.Message;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;

public interface SenderNotificationStrategy {

    Message generateSenderNotificationMessage(PaymentNotificationMessageEvent event);
}
