package com.greentechpay.notificationservice.controller.v1;

import com.greentechpay.notificationservice.kafka.dto.LoginDeviceTokenEvent;
import com.greentechpay.notificationservice.model.dto.request.AppUser;
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
    public void create(@RequestBody LoginDeviceTokenEvent loginDeviceTokenEvent) {
        tokenService.create(loginDeviceTokenEvent);
    }

    @PutMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestBody AppUser user) {
        tokenService.logout(user);
    }
}
