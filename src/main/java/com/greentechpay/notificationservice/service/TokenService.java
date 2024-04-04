package com.greentechpay.notificationservice.service;

import com.greentechpay.notificationservice.dto.DeviceTokenDto;
import com.greentechpay.notificationservice.entity.UserDeviceToken;
import com.greentechpay.notificationservice.exception.UserNotFoundException;
import com.greentechpay.notificationservice.mapper.DeviceTokenMapper;
import com.greentechpay.notificationservice.repository.UserDeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final UserDeviceTokenRepository userDeviceTokenRepository;
    private final DeviceTokenMapper deviceTokenMapper;
    private final UserService userService;

    //TODO check validation
    public void create(DeviceTokenDto deviceTokenDto) {
        if (userService.existsById(deviceTokenDto.getUserId())) {
            UserDeviceToken userDeviceToken = deviceTokenMapper.dtoToEntity(deviceTokenDto);
            userDeviceToken.setActive(true);
            userDeviceToken.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            userDeviceTokenRepository.save(userDeviceToken);
        }else {
            throw new UserNotFoundException("User could not find by id: "+ deviceTokenDto.getUserId());
        }
    }

    public String getDeviceTokenByUserId(String userId){
        return userDeviceTokenRepository.getDeviceTokenByUserId(userId);
    }

    protected UserDeviceToken getUserDeviceTokenByUserId(String userId){
        return userDeviceTokenRepository.getUserDeviceTokenByUserId(userId);
    }
}
