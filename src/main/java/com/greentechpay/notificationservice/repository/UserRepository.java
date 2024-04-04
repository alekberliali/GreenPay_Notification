package com.greentechpay.notificationservice.repository;

import com.greentechpay.notificationservice.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, String> {
}
