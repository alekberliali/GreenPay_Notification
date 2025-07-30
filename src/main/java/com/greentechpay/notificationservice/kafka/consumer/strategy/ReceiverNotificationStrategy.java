package com.greentechpay.notificationservice.kafka.consumer.strategy;

import com.google.firebase.messaging.Message;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;

public interface ReceiverNotificationStrategy {

    Message generateReceiverNotificationMessage(PaymentNotificationMessageEvent event);
}
