package com.greentechpay.notificationservice.mapper;

import com.greentechpay.notificationservice.dto.NotificationMessageToAll;
import com.greentechpay.notificationservice.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomNotificationMapper {
    public Notification convertFromPaymentNotificationMessageEvent(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        Notification notification = new Notification();
        notification.setUserId(paymentNotificationMessageEvent.getUserId());
        notification.setTitle(paymentNotificationMessageEvent.getTitle());
        notification.setBody(paymentNotificationMessageEvent.getBody());
        return notification;
    }

    public List<Notification> convertFromNotificationMessageAll(NotificationMessageToAll notificationMessageToAll) {
        List<Notification> notificationList = new ArrayList<>();
        for (String userId : notificationMessageToAll.getUserIdList()) {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle(notificationMessageToAll.getTitle());
            notification.setBody(notificationMessageToAll.getBody());
            notificationList.add(notification);
        }
        return notificationList;
    }
}
