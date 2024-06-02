package com.greentechpay.notificationservice.controller;

import com.greentechpay.notificationservice.dto.*;
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

    @PostMapping("/send-all")
    public ResponseEntity<String> sendAll(@RequestBody NotificationMessageToAll notificationMessageToAll) {
       return ResponseEntity.ok(firebaseMessagingService.sendNotificationToManyUser(notificationMessageToAll));
    }

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody PaymentNotificationMessageEvent paymentNotificationMessageEvent){
        return ResponseEntity.ok(firebaseMessagingService.sendNotificationByToken(paymentNotificationMessageEvent));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<NotificationDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    @PostMapping("/page/{userId}")
    public ResponseEntity<PageResponse<NotificationDto>>
    getAllWithPageByUserId(@PathVariable String userId, @Valid @RequestBody PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(notificationService.getAllByUserId(userId, pageRequestDto));
    }

    @DeleteMapping("/all/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAllByUserId(@PathVariable String userId) {
        notificationService.delete(userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long id) {
        notificationService.deleteById(id);
    }
}
