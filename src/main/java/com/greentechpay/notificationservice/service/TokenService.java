package com.greentechpay.notificationservice.service;

import com.greentechpay.notificationservice.exception.UserNotFoundException;
import com.greentechpay.notificationservice.kafka.dto.LoginDeviceTokenEvent;
import com.greentechpay.notificationservice.model.dto.request.AppUser;
import com.greentechpay.notificationservice.model.entity.UserDeviceToken;
import com.greentechpay.notificationservice.mapper.DeviceTokenMapper;
import com.greentechpay.notificationservice.repository.UserDeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.greentechpay.notificationservice.kafka.KafkaConfigs.*;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final UserDeviceTokenRepository userDeviceTokenRepository;
    private final DeviceTokenMapper deviceTokenMapper;

    @KafkaListener(topics = LOGIN_DEVICE_TOPIC, containerFactory = LOGIN_DEVICE_CONTAINER_FACTORY)
    public void create(LoginDeviceTokenEvent loginDeviceTokenEvent) {
        if (Boolean.FALSE.equals(existsByUserId(loginDeviceTokenEvent.getUserId()))) {
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

    public Boolean existsByUserId(String userId) {
        return userDeviceTokenRepository.existsByUserId(userId);
    }

    public String getDeviceTokenByUserId(String userId) {
        return userDeviceTokenRepository.findDeviceTokenByUserId(userId);
    }

    protected List<String> getDeviceTokenListByUserIdList(List<String> userIdList) {
        return userDeviceTokenRepository.findTokensByUserIds(userIdList);
    }

    public Boolean isTokenValid(String userId) {
        UserDeviceToken userDeviceToken = userDeviceTokenRepository.getUserDeviceTokenByUserId(userId);

        if (userDeviceToken.getDeviceToken() != null) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public void logout(AppUser user) {
        String userId = user.getUserId();
        if (Boolean.TRUE.equals(existsByUserId(userId))) {
            UserDeviceToken userDeviceToken = userDeviceTokenRepository.getUserDeviceTokenByUserId(userId);
            userDeviceToken.setDeviceToken(null);
            userDeviceToken.setUpdatedAt(LocalDateTime.now());
            userDeviceTokenRepository.save(userDeviceToken);
        } else {
            throw new UserNotFoundException("User could not find by id: " + userId);
        }
    }
}
