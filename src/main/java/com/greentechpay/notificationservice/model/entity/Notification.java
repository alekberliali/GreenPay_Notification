package com.greentechpay.notificationservice.model.entity;

import com.greentechpay.notificationservice.model.enumarated.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "notifications", schema = "public")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String body;
    @Column(name = "send_date")
    private LocalDateTime sendDate;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "read_status")
    private Boolean readStatus;
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;
    @Column(name = "merchant_id")
    private Long merchantId;
    @Column(name = "send_status")
    private Boolean sendStatus;
    @Column(name = "sender_type")
    private String senderType;
}
