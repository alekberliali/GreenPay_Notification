package com.greentechpay.notificationservice.kafka;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
@Data
public class KafkaConfigs {
    @Value("${spring.kafka.bootstrap-servers}")
    private String server;

    private static final String PACKAGE_PATH = "com.greentechpay.notificationservice.kafka.dto";

    public static final String NOTIFICATION_EVENT_PATH = PACKAGE_PATH + ".PaymentNotificationMessageEvent";
    public static final String LOGIN_DEVICE_TOKEN_EVENT_PATH = PACKAGE_PATH + ".LoginDeviceTokenEvent";

    public static final String NOTIFICATION_TOPIC = "Notification-Message";
    public static final String LOGIN_DEVICE_TOPIC = "login-device-token";

    public static final String NOTIFICATION_CONTAINER_FACTORY = "kafkaListenerContainerFactoryPaymentNotificationMessage";
    public static final String LOGIN_DEVICE_CONTAINER_FACTORY = "kafkaListenerContainerFactoryLoginDeviceToken";
}
