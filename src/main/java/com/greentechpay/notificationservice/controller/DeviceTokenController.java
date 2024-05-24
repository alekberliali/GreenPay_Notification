package com.greentechpay.notificationservice.controller;

import com.greentechpay.notificationservice.dto.LoginDeviceTokenEvent;
import com.greentechpay.notificationservice.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/device-token")
public class DeviceTokenController {

    private final TokenService tokenService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public void create(@RequestBody LoginDeviceTokenEvent loginDeviceTokenEvent) {
        tokenService.create(loginDeviceTokenEvent);
    }
}
