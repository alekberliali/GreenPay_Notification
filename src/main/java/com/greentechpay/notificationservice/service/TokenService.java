package com.greentechpay.notificationservice.service;

import com.greentechpay.notificationservice.dto.DeviceTokenDto;
import com.greentechpay.notificationservice.entity.UserDeviceToken;
import com.greentechpay.notificationservice.mapper.DeviceTokenMapper;
import com.greentechpay.notificationservice.repository.UserDeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final UserDeviceTokenRepository userDeviceTokenRepository;
    private final DeviceTokenMapper deviceTokenMapper;

    public void create(DeviceTokenDto deviceTokenDto) {
        if (!userDeviceTokenRepository.existsByUserId(deviceTokenDto.getUserId())) {
            UserDeviceToken userDeviceToken = deviceTokenMapper.dtoToEntity(deviceTokenDto);
            userDeviceToken.setCreatedAt(LocalDateTime.now());
            userDeviceTokenRepository.save(userDeviceToken);
        } else if (userDeviceTokenRepository.existsByUserId(deviceTokenDto.getUserId())) {
            UserDeviceToken userDeviceToken = userDeviceTokenRepository.getUserDeviceTokenByUserId(deviceTokenDto.getUserId());
            userDeviceToken.setDeviceToken(deviceTokenDto.getDeviceToken());
            userDeviceToken.setUpdatedAt(LocalDateTime.now());
            userDeviceTokenRepository.save(userDeviceToken);
        }
    }

    public String getDeviceTokenByUserId(String userId) {
        return userDeviceTokenRepository.getDeviceTokenByUserId(userId);
    }

}
