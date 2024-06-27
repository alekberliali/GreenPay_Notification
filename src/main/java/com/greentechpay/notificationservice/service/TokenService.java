package com.greentechpay.notificationservice.service;

import com.greentechpay.notificationservice.dto.LoginDeviceTokenEvent;
import com.greentechpay.notificationservice.entity.UserDeviceToken;
import com.greentechpay.notificationservice.mapper.DeviceTokenMapper;
import com.greentechpay.notificationservice.repository.UserDeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final UserDeviceTokenRepository userDeviceTokenRepository;
    private final DeviceTokenMapper deviceTokenMapper;

    @KafkaListener(topics = "login-device-token", groupId = "2",
            containerFactory = "kafkaListenerContainerFactoryLoginDeviceToken")
    public void create(LoginDeviceTokenEvent loginDeviceTokenEvent) {
        if (!userDeviceTokenRepository.existsByUserId(loginDeviceTokenEvent.getUserId())) {
            UserDeviceToken userDeviceToken = deviceTokenMapper.dtoToEntity(loginDeviceTokenEvent);
            userDeviceToken.setCreatedAt(LocalDateTime.now());
            userDeviceTokenRepository.save(userDeviceToken);
        } else if (userDeviceTokenRepository.existsByUserId(loginDeviceTokenEvent.getUserId())) {
            UserDeviceToken userDeviceToken = userDeviceTokenRepository.getUserDeviceTokenByUserId(loginDeviceTokenEvent.getUserId());
            userDeviceToken.setDeviceToken(loginDeviceTokenEvent.getDeviceToken());
            userDeviceToken.setUpdatedAt(LocalDateTime.now());
            userDeviceTokenRepository.save(userDeviceToken);
        }
    }

    protected String getDeviceTokenByUserId(String userId) {
        return userDeviceTokenRepository.findDeviceTokenByUserId(userId);
    }

    protected List<String> getDeviceTokenListByUserIdList(List<String> userIdList){
        return userDeviceTokenRepository.findTokensByUserIds(userIdList);
    }
}
