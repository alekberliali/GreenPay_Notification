package com.greentechpay.notificationservice.repository;

import com.greentechpay.notificationservice.entity.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
    @Query("select token.deviceToken from UserDeviceToken token where token.userId=:userId")
    String findDeviceTokenByUserId(String userId);

    @Query("SELECT u.deviceToken FROM UserDeviceToken u WHERE u.userId IN :userIdList")
    List<String> findTokensByUserIds(List<String> userIdList);

    UserDeviceToken getUserDeviceTokenByUserId(String userId);

    boolean existsByUserId(String userId);
}
