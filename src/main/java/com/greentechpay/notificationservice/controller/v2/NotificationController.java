package com.greentechpay.notificationservice.controller.v2;

import com.greentechpay.notificationservice.model.dto.request.PageRequestDto;
import com.greentechpay.notificationservice.model.dto.response.NotificationBackDto;
import com.greentechpay.notificationservice.model.dto.response.NotificationDto;
import com.greentechpay.notificationservice.model.dto.response.PageResponse;
import com.greentechpay.notificationservice.model.dto.response.ResponseDto;
import com.greentechpay.notificationservice.model.enumarated.NotificationType;
import com.greentechpay.notificationservice.service.NotificationService;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.greentechpay.notificationservice.utils.HeaderKey.*;
import static com.greentechpay.notificationservice.utils.ParamFilter.NOTIFICATION_TYPE;

@RestController("v2")
@RequiredArgsConstructor
@RequestMapping("api/v2/notification")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/history")
    public ResponseEntity<List<NotificationBackDto>> getAll(@RequestParam @Nullable Long merchantId) {
        return ResponseEntity.ok();
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<NotificationDto> getById(@RequestHeader(AUTHORIZATION) String token, @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(token, id));
    }

    @PostMapping("/page")
    public ResponseEntity<PageResponse<List<NotificationDto>>>
    getAllWithPageByUserId(@RequestHeader(AUTHORIZATION) String token,
                           @RequestParam(NOTIFICATION_TYPE) NotificationType notificationType,
                           @Valid @RequestBody PageRequestDto pageRequestDto) {
        return ResponseEntity.ok(notificationService.getAllListByUserId(token, notificationType, pageRequestDto));
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
