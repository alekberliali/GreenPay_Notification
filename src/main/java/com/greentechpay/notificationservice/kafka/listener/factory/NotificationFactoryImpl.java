package com.greentechpay.notificationservice.kafka.listener.factory;

import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.kafka.listener.strategy.SendMessageStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationFactoryImpl implements NotificationFactory {

    private final Map<String, SendMessageStrategy> messageStrategyMap;

    private SendMessageStrategy getSendMessageStrategy(String messageType) {
        return messageStrategyMap.get(messageType);
    }

    @Override
    public void executeNotification(PaymentNotificationMessageEvent event) {
        SendMessageStrategy sendMessageStrategy = getSendMessageStrategy(event.getTransferType().getValue());
        sendMessageStrategy.sendMessage(event);
    }
}
