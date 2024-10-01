package com.greentechpay.notificationservice.mapper;

import com.greentechpay.notificationservice.model.dto.NotificationMessageToAll;
import com.greentechpay.notificationservice.kafka.dto.PaymentNotificationMessageEvent;
import com.greentechpay.notificationservice.model.enumarated.NotificationType;
import com.greentechpay.notificationservice.model.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomNotificationMapper {
    public Notification convertFromPaymentNotificationMessageEventForSender(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        var body = paymentNotificationMessageEvent.getSender();
        Notification notification = new Notification();
        notification.setTitle(paymentNotificationMessageEvent.getTitle());
        notification.setUserId(paymentNotificationMessageEvent.getSender().getUserId());
        notification.setBody("-" + body.getAmount() + " " + body.getCurrency() + ", " + body.getDate());
        notification.setNotificationType(NotificationType.NOTIFICATION);
        return notification;
    }

    public Notification convertFromPaymentNotificationMessageEventForReceiver(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        var requestBody = paymentNotificationMessageEvent.getReceiver();
        Notification notification = new Notification();
        notification.setUserId(paymentNotificationMessageEvent.getReceiver().getUserId());
        notification.setTitle(paymentNotificationMessageEvent.getTitle());
        notification.setBody("+" + requestBody.getAmount() + " " + requestBody.getCurrency() +
                ", " + requestBody.getDate());
        notification.setNotificationType(NotificationType.NOTIFICATION);
        return notification;
    }

    public List<Notification> convertFromNotificationMessageAll(NotificationMessageToAll notificationMessageToAll) {
        List<Notification> notificationList = new ArrayList<>();
        for (String userId : notificationMessageToAll.getUserIdList()) {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle(notificationMessageToAll.getTitle());
            notification.setBody(notificationMessageToAll.getBody());
            notification.setNotificationType(notification.getNotificationType());
            notificationList.add(notification);
        }
        return notificationList;
    }
}
