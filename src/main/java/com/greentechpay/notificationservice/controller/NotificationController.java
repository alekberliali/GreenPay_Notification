package com.greentechpay.notificationservice.controller;

import com.greentechpay.notificationservice.dto.NotificationDto;
import com.greentechpay.notificationservice.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.dto.PageRequestDto;
import com.greentechpay.notificationservice.dto.PageResponse;
import com.greentechpay.notificationservice.service.FirebaseMessagingService;
import com.greentechpay.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final FirebaseMessagingService firebaseMessagingService;
    private final NotificationService notificationService;

    @PostMapping("/send")
    public String send(@RequestBody PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        return firebaseMessagingService.sendNotificationByToken(paymentNotificationMessageEvent);
    }

    @PostMapping("/page/{userId}")
    public ResponseEntity<PageResponse<NotificationDto>> getAllWithPageByUserId(@PathVariable String userId, @Valid @RequestBody PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(notificationService.getAllByUserId(userId, pageRequestDto));
    }

    @DeleteMapping("/delete/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllByUserId(@PathVariable String userId) {
        notificationService.delete(userId);
    }
}
