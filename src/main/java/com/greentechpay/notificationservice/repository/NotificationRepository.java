package com.greentechpay.notificationservice.repository;

import com.greentechpay.notificationservice.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select notification from Notification notification where notification.userId=:userId " +
            "order by notification.sendDate desc ")
    Page<Notification> findAllByUserId(PageRequest pageRequest, String userId);

    void deleteAllByUserId(String userId);
}
