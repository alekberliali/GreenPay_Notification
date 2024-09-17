package com.greentechpay.notificationservice.controller;

import com.greentechpay.notificationservice.dto.*;
import com.greentechpay.notificationservice.dto.request.PageRequestDto;
import com.greentechpay.notificationservice.dto.response.NotificationDto;
import com.greentechpay.notificationservice.dto.response.PageResponse;
import com.greentechpay.notificationservice.dto.response.ResponseDto;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.service.FirebaseMessagingService;
import com.greentechpay.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.greentechpay.notificationservice.utils.HeaderKey.*;
import static com.greentechpay.notificationservice.utils.ParamFilter.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/notification")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class NotificationController {
    private final FirebaseMessagingService firebaseMessagingService;
    private final NotificationService notificationService;

    @PostMapping("/send-all")
    public ResponseEntity<Integer> sendAll(@RequestBody NotificationMessageToAll notificationMessageToAll)
            throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(firebaseMessagingService.sendNotificationToManyUser(notificationMessageToAll));
    }

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        return ResponseEntity.ok(firebaseMessagingService.sendNotificationByToken(paymentNotificationMessageEvent));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<NotificationDto> getById(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(token, id));
    }

    @PostMapping("/page")
    public ResponseEntity<PageResponse<Map<LocalDate, List<NotificationDto>>>>
    getAllWithPageByUserId(@RequestHeader(AUTHORIZATION) String token,
                           @RequestParam(NOTIFICATION_TYPE) NotificationType notificationType,
                           @Valid @RequestBody PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(notificationService.getAllByUserId(token, notificationType, pageRequestDto));
    }

    @GetMapping("/read-all")
    @ResponseStatus(HttpStatus.OK)
    public void readAll(@RequestHeader(AUTHORIZATION) String token) {
        notificationService.readAll(token);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long id) {
        notificationService.deleteById(token, id);
    }

    @GetMapping("/get-status")
    public ResponseEntity<ResponseDto<Boolean>> getStatus(@RequestHeader(AUTHORIZATION) String token) {
        return ResponseEntity.ok(notificationService.getReadStatusByUserId(token));
    }
}
