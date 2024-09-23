package com.greentechpay.notificationservice.repository;

import com.greentechpay.notificationservice.model.dto.NotificationType;
import com.greentechpay.notificationservice.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("select notification from Notification notification where notification.userId=:userId " +
            "and notification.notificationType=:notificationType order by notification.sendDate desc ")
    Optional<Page<Notification>> findAllByUserId(PageRequest pageRequest, NotificationType notificationType, String userId);

    @Modifying
    @Query("update Notification notification set notification.readStatus=true where notification.userId=:userId")
    void readAll(@Param("userId") String userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.readStatus = false")
    Long countUnreadNotificationsByUserId(@Param("userId") String userId);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Notification e WHERE e.userId = :userId AND e.id = :id")
    Boolean existsByUserIdAndId(@Param("userId") String userId, @Param("id") Long id);
}
