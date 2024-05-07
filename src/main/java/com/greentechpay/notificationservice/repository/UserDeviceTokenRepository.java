package com.greentechpay.notificationservice.repository;

import com.greentechpay.notificationservice.entity.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
    @Query("select token.deviceToken from UserDeviceToken token where token.userId=:userId")
    String getDeviceTokenByUserId(String userId);

    UserDeviceToken getUserDeviceTokenByUserId(String userId);

    boolean existsByUserId(String userId);
}
