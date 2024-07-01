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
}
