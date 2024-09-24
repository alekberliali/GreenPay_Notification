package com.greentechpay.notificationservice.service;

import com.greentechpay.notificationservice.model.dto.NotificationMessageToAll;
import com.greentechpay.notificationservice.model.enumarated.NotificationType;
import com.greentechpay.notificationservice.model.dto.request.PageRequestDto;
import com.greentechpay.notificationservice.model.dto.response.NotificationDto;
import com.greentechpay.notificationservice.model.dto.response.PageResponse;
import com.greentechpay.notificationservice.model.dto.response.ResponseDto;
import com.greentechpay.notificationservice.model.entity.Notification;
import com.greentechpay.notificationservice.exception.NotificationIsNotFound;
import com.greentechpay.notificationservice.exception.UserIsNotFoundException;
import com.greentechpay.notificationservice.jwt.JwtUtil;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.mapper.CustomNotificationMapper;
import com.greentechpay.notificationservice.mapper.NotificationMapper;
import com.greentechpay.notificationservice.model.enumarated.TransferType;
import com.greentechpay.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.greentechpay.notificationservice.utils.ResponseMessage.*;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CustomNotificationMapper customNotificationMapper;
    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private String getUserIdFromToken(String token) {
        String jwt = token.substring(7);
        return jwtUtil.extractUserId(jwt);
    }

    private Boolean existByUserIdAndId(String userId, Long id) {
        return notificationRepository.existsByUserIdAndId(userId, id);
    }

    public void create(PaymentNotificationMessageEvent event) {
        if (event.getTransferType() == null) {
            createUpdateNotification(event);
        } else if (event.getTransferType().equals(TransferType.IbanToUId) ||
                event.getTransferType().equals(TransferType.IbanToIban) ||
                event.getTransferType().equals(TransferType.IbanToPhoneNumber) ||
                event.getTransferType().equals(TransferType.UIdToIban) ||
                event.getTransferType().equals(TransferType.UIdToUId) ||
                event.getTransferType().equals(TransferType.Qr) ||
                event.getTransferType().equals(TransferType.Nfc)) {

            createSenderNotification(event);
            createReceiverNotification(event);
        } else if (event.getTransferType().equals(TransferType.CardToBalance)) {
            createReceiverNotification(event);
        } else if (event.getTransferType().equals(TransferType.BalanceToCard) ||
                event.getTransferType().equals(TransferType.BillingPayment)) {
            createSenderNotification(event);
        } else {
            logger.error("Unsupported transfer type: {}", event.getTransferType());
        }
    }

    private void createUpdateNotification(PaymentNotificationMessageEvent event) {
        Notification notification = new Notification();
        notification.setTitle(event.getTitle());
        notification.setUserId(event.getUserId());
        notification.setSendDate(LocalDateTime.now());
        notification.setReadStatus(Boolean.FALSE);
        notification.setBody(event.getBody().getDescription());
        notification.setNotificationType(NotificationType.NOTIFICATION);
        notificationRepository.save(notification);
    }

    private void createSenderNotification(PaymentNotificationMessageEvent event) {
        var notification = customNotificationMapper.convertFromPaymentNotificationMessageEventForSender(event);
        notification.setSendDate(LocalDateTime.now());
        notification.setReadStatus(false);
        notificationRepository.save(notification);
    }

    private void createReceiverNotification(PaymentNotificationMessageEvent event) {
        var notification = customNotificationMapper.convertFromPaymentNotificationMessageEventForReceiver(event);
        notification.setSendDate(LocalDateTime.now());
        notification.setReadStatus(false);
        notificationRepository.save(notification);
    }

    public void createAll(NotificationMessageToAll notificationMessageToAll) {
        var notificationList = customNotificationMapper.convertFromNotificationMessageAll(notificationMessageToAll);
        for (Notification notification : notificationList) {
            notification.setSendDate(LocalDateTime.now());
            notification.setReadStatus(false);
        }
        notificationRepository.saveAll(notificationList);
    }

    public PageResponse<Map<LocalDate, List<NotificationDto>>>
    getAllByUserId(String token, NotificationType notificationType, PageRequestDto pageRequestDto) {

        String userId = getUserIdFromToken(token);

        var pageRequest = PageRequest.of(pageRequestDto.page(), pageRequestDto.size());
        var result = notificationRepository.findAllByUserId(pageRequest, notificationType, userId)
                .orElseThrow(() -> new UserIsNotFoundException(USER_IS_NOT_FOUND + userId));

        Map<LocalDate, List<NotificationDto>> notifcationMap = new HashMap<>();
        for (Notification dto : result) {
            LocalDate date = dto.getSendDate().toLocalDate();
            List<NotificationDto> notificationDtoList = notifcationMap.getOrDefault(date, new ArrayList<>());
            notificationDtoList.add(notificationMapper.entityToDto(dto));
            notifcationMap.put(date, notificationDtoList);
        }
        Map<LocalDate, List<NotificationDto>> sortedMap = new TreeMap<>(Collections.reverseOrder());
        sortedMap.putAll(notifcationMap);

        return PageResponse.<Map<LocalDate, List<NotificationDto>>>builder()
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .content(sortedMap)
                .build();
    }

    public NotificationDto getById(String token, Long id) {

        String userId = getUserIdFromToken(token);

        if (Boolean.FALSE.equals(existByUserIdAndId(userId, id))) {
            throw new NotificationIsNotFound(NOTIFICATION_ID_IS_NOT_EXIST + id);
        }

        var notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationIsNotFound(NOTIFICATION_ID_IS_NOT_EXIST + id));
        updateNotificationStatus(notification);
        return notificationMapper.entityToDto(notification);
    }

    private void updateNotificationStatus(Notification notification) {
        notification.setReadStatus(true);
        notificationRepository.save(notification);
    }

    public void deleteById(String token, Long id) {
        String userId = getUserIdFromToken(token);

        if (Boolean.FALSE.equals(existByUserIdAndId(userId, id))) {
            throw new NotificationIsNotFound(NOTIFICATION_ID_IS_NOT_EXIST + id);
        }

        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
        } else throw new NotificationIsNotFound(NOTIFICATION_ID_IS_NOT_EXIST + id);
    }

    @Transactional
    public void readAll(String token) {
        String userId = getUserIdFromToken(token);
        notificationRepository.readAll(userId);
    }

    public ResponseDto<Boolean> getReadStatusByUserId(String token) {
        String userId = getUserIdFromToken(token);
        Long count = notificationRepository.countUnreadNotificationsByUserId(userId);
        Boolean result = count > 0;
        return ResponseDto.<Boolean>builder()
                .data(result)
                .build();
    }
}
