package com.greentechpay.notificationservice.controller;

import com.greentechpay.notificationservice.dto.DeviceTokenDto;
import com.greentechpay.notificationservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/device-token")
public class DeviceTokenController {

    private final TokenService tokenService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public void create(@RequestBody DeviceTokenDto deviceTokenDto) {
        tokenService.create(deviceTokenDto);
    }
}
