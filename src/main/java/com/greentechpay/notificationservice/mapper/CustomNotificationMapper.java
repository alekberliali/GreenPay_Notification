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
        var body = paymentNotificationMessageEvent.getBody();
        Notification notification = new Notification();
        notification.setUserId(paymentNotificationMessageEvent.getUserId());
        notification.setTitle(paymentNotificationMessageEvent.getTitle());
        notification.setBody("-" + body.getAmount() + " " + body.getCurrency() +
                ", " + body.getRequestField() + " " + body.getDate());
        return notification;
    }

    public Notification convertFromPaymentNotificationMessageEventForReceiver(PaymentNotificationMessageEvent paymentNotificationMessageEvent) {
        var requestBody = paymentNotificationMessageEvent.getReceiverBody();
        Notification notification = new Notification();
        notification.setUserId(paymentNotificationMessageEvent.getReceiverUserId());
        notification.setTitle(paymentNotificationMessageEvent.getTitle());
        notification.setBody("+" + requestBody.getAmount() + " " + requestBody.getCurrency() +
                ", " + requestBody.getRequestField() + " " + requestBody.getDate());
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
