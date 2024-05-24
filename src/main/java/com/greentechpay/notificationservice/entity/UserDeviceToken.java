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
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "user_device_tokens", schema = "public")
public class UserDeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "device_token")
    private String deviceToken;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "user_id")
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDeviceToken that)) return false;

        if (!id.equals(that.id)) return false;
        if (!deviceToken.equals(that.deviceToken)) return false;
        if (!createdAt.equals(that.createdAt)) return false;
        if (!Objects.equals(updatedAt, that.updatedAt)) return false;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + deviceToken.hashCode();
        result = 31 * result + createdAt.hashCode();
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + userId.hashCode();
        return result;
    }
}
