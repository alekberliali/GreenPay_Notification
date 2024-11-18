package com.greentechpay.notificationservice.kafka;

import com.greentechpay.notificationservice.kafka.dto.LoginDeviceTokenEvent;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.kafka.dto.SimaNotificationMessageEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static com.greentechpay.notificationservice.kafka.KafkaConfigs.*;
import static org.apache.kafka.clients.consumer.OffsetResetStrategy.EARLIEST;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaConfigs kafkaConfigs;

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigs.getServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "7");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST.toString());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }

    @Bean
    public ConsumerFactory<String, LoginDeviceTokenEvent> consumerFactoryLoginDeviceToken() {
        Map<String, Object> props = consumerConfigs();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LOGIN_DEVICE_TOKEN_EVENT_PATH);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LoginDeviceTokenEvent>
    kafkaListenerContainerFactoryLoginDeviceToken() {
        ConcurrentKafkaListenerContainerFactory<String, LoginDeviceTokenEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryLoginDeviceToken());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, PaymentNotificationMessageEvent> consumerFactoryPaymentNotificationMessage() {
        Map<String, Object> props = consumerConfigs();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, PAYMENT_NOTIFICATION_EVENT_PATH);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentNotificationMessageEvent>
    kafkaListenerContainerFactoryPaymentNotificationMessage() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentNotificationMessageEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryPaymentNotificationMessage());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, SimaNotificationMessageEvent> consumerFactorySimaNotificationMessage() {
        Map<String, Object> props = consumerConfigs();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, SIMA_NOTIFICATION_EVENT_PATH);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SimaNotificationMessageEvent>
            kafkaListenerContainerFactorySimaNotificationMessage(){
        ConcurrentKafkaListenerContainerFactory<String, SimaNotificationMessageEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactorySimaNotificationMessage());
        return factory;
    }
}
