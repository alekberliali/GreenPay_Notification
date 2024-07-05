package com.greentechpay.notificationservice.service;

import com.greentechpay.notificationservice.dto.*;
import com.greentechpay.notificationservice.entity.Notification;
import com.greentechpay.notificationservice.mapper.CustomNotificationMapper;
import com.greentechpay.notificationservice.mapper.NotificationMapper;
import com.greentechpay.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CustomNotificationMapper customNotificationMapper;

    public void create(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        var notification = customNotificationMapper.convertFromPaymentNotificationMessageEvent(paymentNotificationMessageEvent);
        notification.setSendDate(LocalDateTime.now());
        notification.setReadStatus(false);
        notification.setNotificationType(NotificationType.NOTIFICATION);
        notificationRepository.save(notification);
        if (paymentNotificationMessageEvent.getReceiverUserId() != null) {
            var receiverNotification = customNotificationMapper
                    .convertFromPaymentNotificationMessageEventForReceiver(paymentNotificationMessageEvent);
            receiverNotification.setSendDate(LocalDateTime.now());
            receiverNotification.setReadStatus(false);
            receiverNotification.setNotificationType(NotificationType.NOTIFICATION);
            notificationRepository.save(receiverNotification);
        }
    }

    public void createAll(NotificationMessageToAll notificationMessageToAll) {
        var notificationList = customNotificationMapper.convertFromNotificationMessageAll(notificationMessageToAll);
        for (Notification notification : notificationList) {
            notification.setSendDate(LocalDateTime.now());
            notification.setReadStatus(false);
        }
        notificationRepository.saveAll(notificationList);
    }

    /*public PageResponse<NotificationDto> getAllByUserId(String userId, PageRequestDto pageRequestDto) {

        var pageRequest = PageRequest.of(pageRequestDto.page(), pageRequestDto.size());
        var result = notificationRepository.findAllByUserId(pageRequest, userId);
        return PageResponse.<NotificationDto>builder()
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(result.getContent().stream()
                        .map(notificationMapper::entityToDto)
                        .toList())
                .build();
    }*/

    public PageResponse<Map<LocalDate, List<NotificationDto>>> getAllByUserId(String userId, PageRequestDto pageRequestDto) {
        var pageRequest = PageRequest.of(pageRequestDto.page(), pageRequestDto.size());
        var result = notificationRepository.findAllByUserId(pageRequest, userId);

        Map<LocalDate, List<NotificationDto>> notifcationMap = new HashMap<>();
        for (Notification dto : result) {
            LocalDate date = dto.getSendDate().toLocalDate();
            List<NotificationDto> notificationDtoList = notifcationMap.getOrDefault(date, new ArrayList<>());
            notificationDtoList.add(notificationMapper.entityToDto(dto));
            notifcationMap.put(date,notificationDtoList);
        }
        Map<LocalDate,List<NotificationDto>> sortedMap= new TreeMap<>(Collections.reverseOrder());
        sortedMap.putAll(notifcationMap);

        return PageResponse.<Map<LocalDate, List<NotificationDto>>> builder()
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .content(sortedMap)
                .build();
    }

    public NotificationDto getById(Long id) {
        var notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("notification could not find by id: " + id));
        updateNotificationStatus(notification);
        return notificationMapper.entityToDto(notification);
    }

    private void updateNotificationStatus(Notification notification) {
        notification.setReadStatus(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void delete(String userId) {
        notificationRepository.deleteAllByUserId(userId);
    }

    public void deleteById(Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
        } else throw new RuntimeException("notification could not find by id: " + id);
    }
}
