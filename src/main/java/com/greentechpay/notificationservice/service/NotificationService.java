package com.greentechpay.notificationservice.service;

import com.greentechpay.notificationservice.dto.NotificationDto;
import com.greentechpay.notificationservice.dto.NotificationType;
import com.greentechpay.notificationservice.dto.PageRequestDto;
import com.greentechpay.notificationservice.dto.PageResponse;
import com.greentechpay.notificationservice.entity.Notification;
import com.greentechpay.notificationservice.mapper.NotificationMapper;
import com.greentechpay.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public void create(Notification notification) {
        notification.setSendDate(LocalDateTime.now());
        notification.setReadStatus(false);
        notification.setNotificationType(NotificationType.NOTIFICATION);
        notificationRepository.save(notification);
    }

    public void createAll(List<Notification> notificationList) {
        for (Notification notification : notificationList) {
            notification.setSendDate(LocalDateTime.now());
            notification.setReadStatus(false);
            notification.setNotificationType(NotificationType.CAMPAIGN);
        }
        notificationRepository.saveAll(notificationList);
    }

    public PageResponse<NotificationDto> getAllByUserId(String userId, PageRequestDto pageRequestDto) {

        var pageRequest = PageRequest.of(pageRequestDto.page(), pageRequestDto.size());
        var result = notificationRepository.findAllByUserId(pageRequest, userId);
        return PageResponse.<NotificationDto>builder()
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .content(result.getContent().stream()
                        .map(notificationMapper::entityToDto)
                        .toList())
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
