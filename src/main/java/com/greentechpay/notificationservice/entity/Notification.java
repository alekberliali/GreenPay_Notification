package com.greentechpay.notificationservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification that)) return false;

        if (!id.equals(that.id)) return false;
        if (!title.equals(that.title)) return false;
        if (!body.equals(that.body)) return false;
        if (!sendDate.equals(that.sendDate)) return false;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + body.hashCode();
        result = 31 * result + sendDate.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }
}
