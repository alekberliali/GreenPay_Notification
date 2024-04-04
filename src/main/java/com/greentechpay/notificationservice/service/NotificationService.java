package com.greentechpay.notificationservice.service;

import com.greentechpay.notificationservice.dto.NotificationDto;
import com.greentechpay.notificationservice.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.dto.PageRequestDto;
import com.greentechpay.notificationservice.dto.PageResponse;
import com.greentechpay.notificationservice.entity.Notification;
import com.greentechpay.notificationservice.exception.UserNotFoundException;
import com.greentechpay.notificationservice.mapper.NotificationMapper;
import com.greentechpay.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserService userService;

    public void create(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        Notification notification = new Notification();
        notification.setTitle(paymentNotificationMessageEvent.getTitle());
        notification.setBody(paymentNotificationMessageEvent.getBody());
        notification.setUser(userService.getUserById(paymentNotificationMessageEvent.getUserId()));
        notification.setSendDate(Timestamp.valueOf(LocalDateTime.now()));
        notificationRepository.save(notification);
    }

    public PageResponse<NotificationDto> getAllByUserId(String userId, PageRequestDto pageRequestDto) {
        if (userService.existsById(userId)) {

            var pageRequest = PageRequest.of(pageRequestDto.page(), pageRequestDto.size());
            var result = notificationRepository.findAllByUserId(pageRequest, userId);
            return PageResponse.<NotificationDto>builder()
                    .totalElements(result.getTotalElements())
                    .totalPages(result.getTotalPages())
                    .content(result.getContent().stream()
                            .map(notificationMapper::entityToDto)
                            .toList())
                    .build();
        } else {
            throw new UserNotFoundException("user could not find by id: " + userId);
        }
    }

    @Transactional
    public void delete(String userId) {
        if (userService.existsById(userId)) {
            notificationRepository.deleteAllByUserId(userId);
        } else throw new UserNotFoundException("User could not find by id: " + userId);
    }
}
